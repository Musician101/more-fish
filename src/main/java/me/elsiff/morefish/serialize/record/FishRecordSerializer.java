package me.elsiff.morefish.serialize.record;

import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.records.FishRecord;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

@NullMarked
public class FishRecordSerializer implements TypeSerializer<FishRecord> {

    private static final RequiredKey<Fish> FISH = ConfigKey.requiredKey("fish", Fish.class);
    private static final RequiredKey<UUID> FISHER = ConfigKey.requiredKey("fisher", UUID.class);
    private static final RequiredKey<Long> TIMESTAMP = ConfigKey.requiredKey("timestamp", Long.class);

    @Override
    public FishRecord deserialize(Type type, ConfigurationNode node) throws SerializationException {
        UUID fisher = FISHER.get(node);
        Fish fish = FISH.get(node);
        long timestamp = TIMESTAMP.get(node);
        return new FishRecord(fisher, fish, timestamp);
    }

    @Override
    public void serialize(Type type, @Nullable FishRecord obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            FISHER.set(node, obj.fisher());
            FISH.set(node, obj.fish());
            TIMESTAMP.set(node, obj.timestamp());
        }
    }
}
