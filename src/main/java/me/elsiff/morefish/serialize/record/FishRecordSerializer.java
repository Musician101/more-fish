package me.elsiff.morefish.serialize.record;

import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.records.FishRecord;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

@NullMarked
public class FishRecordSerializer implements TypeSerializer<FishRecord> {

    @Override
    public FishRecord deserialize(Type type, ConfigurationNode node) throws SerializationException {
        UUID fisher = node.node("fisher").require(UUID.class);
        Fish fish = node.node("fish").require(Fish.class);
        long timestamp = node.node("timestamp").require(Long.class);
        return new FishRecord(fisher, fish, timestamp);
    }

    @Override
    public void serialize(Type type, @Nullable FishRecord obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.node("fisher").set(obj.fisher());
            node.node("fish").set(obj.fish());
            node.node("timestamp").set(obj.timestamp());
        }
    }
}
