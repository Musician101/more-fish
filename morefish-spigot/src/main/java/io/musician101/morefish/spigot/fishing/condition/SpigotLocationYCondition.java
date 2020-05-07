package io.musician101.morefish.spigot.fishing.condition;

import javax.annotation.Nonnull;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotLocationYCondition implements SpigotFishCondition {

    private final DoubleRange range;

    public SpigotLocationYCondition(@Nonnull DoubleRange range) {
        this.range = range;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return range.containsDouble(fisher.getLocation().getY());
    }
}
