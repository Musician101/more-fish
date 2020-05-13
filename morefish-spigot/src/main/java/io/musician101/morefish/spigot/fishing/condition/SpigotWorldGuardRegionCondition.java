package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.spigot.SpigotMoreFish;
import javax.annotation.Nonnull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotWorldGuardRegionCondition implements SpigotFishCondition {

    private final String regionId;

    public SpigotWorldGuardRegionCondition(@Nonnull String regionId) {
        this.regionId = regionId;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return SpigotMoreFish.getInstance().getWorldGuardHooker().containsLocation(regionId, caught.getLocation());
    }
}
