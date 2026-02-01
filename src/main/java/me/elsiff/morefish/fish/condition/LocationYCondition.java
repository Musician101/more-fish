package me.elsiff.morefish.fish.condition;

import me.elsiff.morefish.util.Range;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LocationYCondition extends FishCondition<Range<Double>> {

    public LocationYCondition(Range<Double> value) {
        super(value);
    }


    public boolean check(Item caught, Player fisher) {
        return value.containsDouble(fisher.getLocation().getY());
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            if (arguments.hasNext()) {
                String key = arguments.pop().value();
                switch (key) {
                    case "minimum" -> {
                        return Tag.preProcessParsed(value.min() + "");
                    }
                    case "maximum" -> {
                        return Tag.preProcessParsed(value.max() + "");
                    }
                }
            }

            return Tag.preProcessParsed(value.min() + "-" + value.max());
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("location-y");
    }
}
