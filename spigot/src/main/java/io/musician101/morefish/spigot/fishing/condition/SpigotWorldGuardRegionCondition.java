package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import io.musician101.morefish.spigot.SpigotMoreFish;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;

public record SpigotWorldGuardRegionCondition(String regionId) implements FishCondition {

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return SpigotMoreFish.getInstance().getWorldGuardHooker().containsLocation(regionId, Bukkit.getEntity(caught).getLocation());
    }
}
