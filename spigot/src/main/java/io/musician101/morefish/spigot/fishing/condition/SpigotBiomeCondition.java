package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;

public record SpigotBiomeCondition(Collection<Biome> biomes) implements FishCondition {

    @SuppressWarnings("ConstantConditions")
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        Entity entity = Bukkit.getEntity(caught);
        Location location = entity.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return biomes.contains(entity.getWorld().getBiome(x, y, z));
    }
}
