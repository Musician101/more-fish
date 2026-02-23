package me.elsiff.morefish.serialize;

import me.elsiff.morefish.serialize.ConfigKey.NonRequiredKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.Objects;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullUnmarked
@SuppressWarnings("unchecked")
public sealed abstract class ConfigKey<V> permits NonRequiredKey, RequiredKey {

    protected final Type type;
    protected final String key;

    private ConfigKey(String key, Type type) {
        this.key = key;
        this.type = type;
    }

    public static <V> RequiredKey<V> requiredKey(String key, Type type) {
        return new RequiredKey<>(key, type);
    }

    public static <V> NonRequiredKey<V> nonRequiredKey(String key, Type type) {
        return nonRequiredKey(key, type, null);
    }

    public static <V> NonRequiredKey<V> nonRequiredKey(String key, Type type, @Nullable V defaultValue) {
        return new NonRequiredKey<>(key, type, defaultValue);
    }

    protected abstract Object getInternal(ConfigurationNode parent) throws SerializationException;

    public V get(ConfigurationNode parent) throws SerializationException {
        try {
            return (V) getInternal(parent);
        }
        catch (ClassCastException e) {
            throw new SerializationException(type + " is not a valid type for " + parent.node(key).path());
        }
    }

    public abstract void set(ConfigurationNode parent, @Nullable V value) throws SerializationException;

    public static final class RequiredKey<V> extends ConfigKey<V> {

        private RequiredKey(String key, Type type) {
            super(key, type);
        }

        @Override
        protected Object getInternal(ConfigurationNode parent) throws SerializationException {
            return parent.node(key).require(type);
        }

        @Override
        public void set(ConfigurationNode parent, @Nullable V value) throws SerializationException {
            parent.node(key).set(value);
        }
    }

    public static final class NonRequiredKey<V> extends ConfigKey<V> {

        @Nullable
        private final V defaultValue;

        private NonRequiredKey(String key, Type type, @Nullable V defaultValue) {
            super(key, type);
            this.defaultValue = defaultValue;
        }

        @Nullable
        @Override
        protected Object getInternal(ConfigurationNode parent) throws SerializationException {
            Object value = parent.node(key).get(type);
            return value == null ? defaultValue : value;
        }

        @Override
        public void set(ConfigurationNode parent, @Nullable V value) throws SerializationException {
            if (getPlugin().getConfig().getBoolean("general.trim-fish-configs") && Objects.equals(defaultValue, value)) {
                parent.node(key).set(null);
                return;
            }

            parent.node(key).set(value);
        }
    }
}
