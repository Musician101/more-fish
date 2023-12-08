package me.elsiff.morefish.common.fishing.condition;

import java.util.Optional;
import java.util.stream.Stream;
import me.elsiff.morefish.common.util.NumberUtils.Range;
import org.jetbrains.annotations.NotNull;

public enum TimeState {
    DAY(new Range<>(1000L, 13000L)),
    NIGHT(new Range<>(0L, 1000L), new Range<>(13000L, 24000L));

    @NotNull private final Range<Long>[] range;

    @SafeVarargs
    TimeState(@NotNull Range<Long>... range) {
        this.range = range;
    }

    @NotNull
    public static Optional<TimeState> fromTime(long worldTime) {
        return Stream.of(values()).filter(state -> Stream.of(state.range).anyMatch(range -> range.containsLong(worldTime))).findFirst();
    }
}
