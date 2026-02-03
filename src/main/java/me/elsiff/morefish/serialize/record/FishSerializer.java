package me.elsiff.morefish.serialize.record;

import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class FishSerializer implements TypeSerializer<Fish> {

    private static final RequiredKey<FishType> TYPE = ConfigKey.requiredKey("type", FishType.class);
    private static final RequiredKey<Double> LENGTH = ConfigKey.requiredKey("length", Double.class);

    @Override
    public Fish deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishType fishType = TYPE.get(node);
        double length = LENGTH.get(node);
        if (length <= 0) {
            throw new SerializationException("'length' record in " + node.path() + " must be greater than 0.");
        }

        return new Fish(fishType, length);
    }

    @Override
    public void serialize(Type type, @Nullable Fish obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            return;
        }

        TYPE.set(node, obj.type());
        LENGTH.set(node, obj.length());
    }
}
