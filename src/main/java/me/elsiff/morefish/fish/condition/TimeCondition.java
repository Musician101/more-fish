package me.elsiff.morefish.fish.condition;

import me.elsiff.morefish.fish.condition.TimeCondition.TimeState;
import me.elsiff.morefish.util.Range;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

@NullMarked
public class TimeCondition extends FishCondition<TimeState> {

	public TimeCondition(TimeState value) {
		super(value);
	}


    public boolean check(Item caught, Player fisher) {
        return TimeState.fromTime(caught.getWorld().getTime()).filter(timeState -> timeState == value).isPresent();
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            return Tag.preProcessParsed(value.toString());
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("time");
    }

    public enum TimeState {
        ANY(new Range<>(0L, 24000L)),
        DAY(new Range<>(1000L, 13000L)),
        NIGHT(new Range<>(0L, 1000L), new Range<>(13000L, 24000L));

        private final Range<Long>[] range;

        @SafeVarargs
        TimeState(Range<Long>... range) {
            this.range = range;
        }

        static Optional<TimeState> fromTime(long worldTime) {
            return Stream.of(values()).filter(state -> Stream.of(state.range).anyMatch(range -> range.containsLong(worldTime))).findFirst();
        }
    }
}
