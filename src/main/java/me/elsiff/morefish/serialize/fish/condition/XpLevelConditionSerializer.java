package me.elsiff.morefish.serialize.fish.condition;

import me.elsiff.morefish.fish.condition.XpLevelCondition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class XpLevelConditionSerializer extends FishConditionSerializer<XpLevelCondition> {

    @Override
    public XpLevelCondition deserialize(Type type, ConfigurationNode node) {
        return new XpLevelCondition(node.getInt());
    }

    @Override
    public void serialize(Type type, @Nullable XpLevelCondition obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(obj.value());
        }
    }
}
