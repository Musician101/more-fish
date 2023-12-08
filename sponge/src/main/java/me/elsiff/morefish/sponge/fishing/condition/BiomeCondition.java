package me.elsiff.morefish.sponge.fishing.condition;

import java.util.Collection;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.biome.Biome;
import org.spongepowered.api.world.server.ServerLocation;

public record BiomeCondition(
        @NotNull Collection<Biome> biomes) implements FishCondition<SpongeFishingCompetition, Item, ServerPlayer> {

    public boolean check(@NotNull Item caught, @NotNull ServerPlayer fisher, @NotNull SpongeFishingCompetition fishingCompetition) {
        ServerLocation location = caught.serverLocation();
        int x = location.blockX();
        int y = location.blockY();
        int z = location.blockZ();
        return biomes.contains(caught.world().biome(x, y, z));
    }
}
