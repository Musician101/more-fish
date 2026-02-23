package me.elsiff.morefish.serialize.fish.condition;

import me.elsiff.morefish.fish.condition.LocationYCondition;
import me.elsiff.morefish.util.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class LocationYConditionSerializer extends FishConditionSerializer<LocationYCondition> {

    @Override
    public @Nullable LocationYCondition deserialize(Type type, ConfigurationNode node) {
        String arg = node.getString();
        if (arg == null || !arg.contains("-")) {
            return null;
        }

        String[] args = arg.split("-");
        if (args.length <= 1) {
            return null;
        }

        return new LocationYCondition(new Range<>(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
    }

    @Override
    public void serialize(Type type, @Nullable LocationYCondition obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(obj.value().toString());
        }
    }
}
