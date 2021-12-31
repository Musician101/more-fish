package me.elsiff.morefish.fishing.condition;

import java.util.Collection;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public record BiomeCondition(@Nonnull Collection<Biome> biomes) implements FishCondition {

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        Location location = caught.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return biomes.contains(caught.getWorld().getBiome(x, y, z));
    }
}
