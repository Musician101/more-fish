package me.elsiff.morefish.serialize.record;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

@NullMarked
public class UUIDSerializer implements TypeSerializer<UUID> {

    @Override
    public UUID deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return UUID.fromString(node.require(String.class));
    }

    @Override
    public void serialize(Type type, @Nullable UUID obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            return;
        }

        node.set(obj.toString());
    }
}
