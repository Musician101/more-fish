package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Bukkit;

public record SpigotTimeCondition(TimeState state) implements FishCondition {

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return TimeState.fromTime(Bukkit.getPlayer(caught).getWorld().getTime()).filter(timeState -> timeState == state).isPresent();
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
