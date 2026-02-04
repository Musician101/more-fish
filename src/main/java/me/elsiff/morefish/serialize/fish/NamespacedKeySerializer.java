package me.elsiff.morefish.serialize.fish;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class NamespacedKeySerializer implements TypeSerializer<NamespacedKey> {

    @Override
    public NamespacedKey deserialize(Type type, ConfigurationNode node) throws SerializationException {
        NamespacedKey key = NamespacedKey.fromString(node.require(String.class));
        if (key == null) {
            throw new SerializationException(node.path() + " has a malformed namespace ID.");
        }

        return key;
    }

    @Override
    public void serialize(Type type, @Nullable NamespacedKey obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(obj.asString());
        }
    }
}
