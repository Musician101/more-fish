package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;

public record SpigotRainingCondition(boolean raining) implements FishCondition {

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return Bukkit.getEntity(caught).getWorld().hasStorm() == this.raining;
    }
}
