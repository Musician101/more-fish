package io.musician101.morefish.spigot.fishing.condition;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotTimeCondition implements SpigotFishCondition {

    private final TimeState state;

    public SpigotTimeCondition(@Nonnull TimeState state) {
        this.state = state;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return TimeState.fromTime(caught.getWorld().getTime()).filter(timeState -> timeState == state).isPresent();
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
            return Stream.of(values()).filter(state -> Stream.of(state.range).anyMatch(range -> range.containsInteger(worldTime))).findFirst();
        }
    }
}
