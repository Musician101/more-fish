package me.elsiff.morefish.fishing.condition;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record XpLevelCondition(int minLevel) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher) {
        return fisher.getLevel() >= this.minLevel;
    }
}
