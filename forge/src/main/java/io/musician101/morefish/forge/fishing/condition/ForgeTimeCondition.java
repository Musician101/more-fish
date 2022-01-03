package io.musician101.morefish.forge.fishing.condition;

import io.musician101.morefish.forge.util.NumberUtils.IntRange;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class ForgeTimeCondition implements ForgeFishCondition {

    private final TimeState state;

    public ForgeTimeCondition(@Nonnull TimeState state) {
        this.state = state;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return TimeState.fromTime(caught.getEntityWorld().getGameTime()).filter(timeState -> timeState == state).isPresent();
    }

    public enum TimeState {
        DAY(new IntRange(1000, 13000)),
        NIGHT(new IntRange(0, 1000), new IntRange(13000, 24000));

        @Nonnull
        private final IntRange[] range;

        TimeState(@Nonnull IntRange... range) {
            this.range = range;
        }

        @Nonnull
        static Optional<TimeState> fromTime(long worldTime) {
            return Stream.of(values()).filter(state -> Stream.of(state.range).anyMatch(range -> range.containsInteger((int) worldTime))).findFirst();
        }
    }
}
