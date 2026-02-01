package me.elsiff.morefish.serialize.fish.condition;

import me.elsiff.morefish.fish.condition.ThunderingCondition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class ThunderingConditionSerializer extends FishConditionSerializer<ThunderingCondition> {

    @Override
    public ThunderingCondition deserialize(Type type, ConfigurationNode node) {
        return new ThunderingCondition(node.getBoolean());
    }

    @Override
    public void serialize(Type type, @Nullable ThunderingCondition obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(obj.value());
        }
    }
}
