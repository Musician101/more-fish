package io.musician101.morefish.spigot.fishing.condition;

import javax.annotation.Nonnull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotThunderingCondition implements SpigotFishCondition {

    private final boolean thundering;

    public SpigotThunderingCondition(boolean thundering) {
        this.thundering = thundering;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return caught.getWorld().isThundering() == this.thundering;
    }
}
