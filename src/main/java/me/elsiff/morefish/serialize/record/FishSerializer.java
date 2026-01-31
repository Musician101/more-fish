package me.elsiff.morefish.serialize.record;

import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class FishSerializer implements TypeSerializer<Fish> {

    @Override
    public Fish deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishType fishType = node.node("type").require(FishType.class);
        double length = node.node("length").require(Double.class);
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

        node.node("type").set(obj.type());
        node.node("length").set(obj.length());
    }
}
