package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;

public record SpigotXPLevelCondition(int minLevel) implements FishCondition {

    @SuppressWarnings("ConstantConditions")
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return Bukkit.getPlayer(fisher).getLevel() >= this.minLevel;
    }
}
