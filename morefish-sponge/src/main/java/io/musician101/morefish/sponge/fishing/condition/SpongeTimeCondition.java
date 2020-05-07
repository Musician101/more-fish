package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.sponge.util.NumberUtils.IntRange;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongeTimeCondition implements SpongeFishCondition {

    private final TimeState state;

    public SpongeTimeCondition(@Nonnull TimeState state) {
        this.state = state;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return TimeState.fromTime(caught.getWorld().getProperties().getWorldTime()).filter(timeState -> timeState == state).isPresent();
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
