package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.Bukkit;

public record SpigotLocationYCondition(DoubleRange range) implements FishCondition {

    @SuppressWarnings("ConstantConditions")
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return range.containsDouble(Bukkit.getPlayer(fisher).getLocation().getY());
    }
}
