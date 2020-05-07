package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.spigot.hooker.SpigotWorldGuardHooker;
import javax.annotation.Nonnull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotWorldGuardRegionCondition implements SpigotFishCondition {

    private final String regionId;
    private final SpigotWorldGuardHooker worldGuardHooker;

    public SpigotWorldGuardRegionCondition(@Nonnull SpigotWorldGuardHooker worldGuardHooker, @Nonnull String regionId) {
        this.worldGuardHooker = worldGuardHooker;
        this.regionId = regionId;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return worldGuardHooker.containsLocation(regionId, caught.getLocation());
    }
}
