package io.musician101.morefish.spigot.fishing.condition;

import javax.annotation.Nonnull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotXPLevelCondition implements SpigotFishCondition {

    private final int minLevel;

    public SpigotXPLevelCondition(int minLevel) {
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return fisher.getLevel() >= this.minLevel;
    }
}
