package me.elsiff.morefish.fish.condition;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record BiomeCondition(@NotNull Collection<Biome> biomes) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher) {
        Location location = caught.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return biomes.contains(caught.getWorld().getBiome(x, y, z));
    }
}
