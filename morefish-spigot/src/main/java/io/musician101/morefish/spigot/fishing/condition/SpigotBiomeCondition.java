package io.musician101.morefish.spigot.fishing.condition;

import java.util.Collection;
import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotBiomeCondition implements SpigotFishCondition {

    private final Collection<Biome> biomes;

    public SpigotBiomeCondition(@Nonnull Collection<Biome> biomes) {
        this.biomes = biomes;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        Location location = caught.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return biomes.contains(caught.getWorld().getBiome(x, y, z));
    }
}
