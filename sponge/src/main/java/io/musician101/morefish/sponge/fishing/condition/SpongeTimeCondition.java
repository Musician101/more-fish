package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import io.musician101.morefish.sponge.util.NumberUtils.IntRange;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;

public final class SpongeTimeCondition implements FishCondition {

    private final TimeState state;

    public SpongeTimeCondition(@Nonnull TimeState state) {
        this.state = state;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return Sponge.server().worldManager().worlds().stream().anyMatch(world -> world.entity(caught).flatMap(entity -> TimeState.fromTime(world.properties().dayTime().asTicks().ticks()).filter(s -> s == state)).isPresent());
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
