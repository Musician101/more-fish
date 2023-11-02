package me.elsiff.morefish.fishing.condition;

import java.util.Optional;
import java.util.stream.Stream;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.util.NumberUtils.Range;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record TimeCondition(@NotNull TimeState state) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull FishingCompetition competition) {
        return TimeState.fromTime(caught.getWorld().getTime()).filter(timeState -> timeState == state).isPresent();
    }

    public enum TimeState {
        DAY(new Range<>(1000L, 13000L)),
        NIGHT(new Range<>(0L, 1000L), new Range<>(13000L, 24000L));

        @NotNull
        private final Range<Long>[] range;

        @SafeVarargs
        TimeState(@NotNull Range<Long>... range) {
            this.range = range;
        }

        @NotNull
        static Optional<TimeState> fromTime(long worldTime) {
            return Stream.of(values()).filter(state -> Stream.of(state.range).anyMatch(range -> range.containsLong(worldTime))).findFirst();
        }
    }
}
