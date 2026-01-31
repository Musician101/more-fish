package me.elsiff.morefish.serialize.fish.condition;

import me.elsiff.morefish.fish.condition.RainingCondition;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class RainingConditionSerializer extends FishConditionSerializer<RainingCondition> {

    @Override
    public RainingCondition deserialize(Type type, ConfigurationNode node) {
        return new RainingCondition(node.getBoolean());
    }

    @Override
    public void serialize(Type type, @Nullable RainingCondition obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(obj.value());
        }
    }
}
