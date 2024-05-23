package me.elsiff.morefish.fishing.condition;

import me.elsiff.morefish.util.NumberUtils.Range;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record LocationYCondition(@NotNull Range<Double> range) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher) {
        return range.containsDouble(fisher.getLocation().getY());
    }
}
