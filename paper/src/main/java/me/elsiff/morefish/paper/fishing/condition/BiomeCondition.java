package me.elsiff.morefish.paper.fishing.condition;

import java.util.Collection;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record BiomeCondition(
        @NotNull Collection<Biome> biomes) implements FishCondition<PaperFishingCompetition, Item, Player> {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull PaperFishingCompetition fishingCompetition) {
        Location location = caught.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return biomes.contains(caught.getWorld().getBiome(x, y, z));
    }
}
