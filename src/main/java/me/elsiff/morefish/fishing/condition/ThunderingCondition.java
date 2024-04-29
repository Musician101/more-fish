package me.elsiff.morefish.fishing.condition;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ThunderingCondition(boolean thundering) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher) {
        return caught.getWorld().isThundering() == this.thundering;
    }
}
