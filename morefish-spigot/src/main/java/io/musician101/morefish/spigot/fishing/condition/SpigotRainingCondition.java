package io.musician101.morefish.spigot.fishing.condition;

import javax.annotation.Nonnull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotRainingCondition implements SpigotFishCondition {

    private final boolean raining;

    public SpigotRainingCondition(boolean raining) {
        this.raining = raining;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return caught.getWorld().hasStorm() == this.raining;
    }
}
