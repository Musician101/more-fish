package me.elsiff.morefish.serialize.fish.condition;

import me.elsiff.morefish.fish.condition.TimeCondition;
import me.elsiff.morefish.fish.condition.TimeCondition.TimeState;
import me.elsiff.morefish.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class TimeConditionSerializer extends FishConditionSerializer<TimeCondition> {

    @Override
    public TimeCondition deserialize(Type type, ConfigurationNode node) {
        return new TimeCondition(EnumUtils.get(node.getString(), TimeState.class, TimeState.ANY));
    }

    @Override
    public void serialize(Type type, @Nullable TimeCondition obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(obj.toString().toLowerCase());
        }
    }
}
