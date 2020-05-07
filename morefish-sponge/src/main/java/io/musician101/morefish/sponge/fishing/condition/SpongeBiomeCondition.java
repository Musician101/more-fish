package io.musician101.morefish.sponge.fishing.condition;

import java.util.Collection;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

public final class SpongeBiomeCondition implements SpongeFishCondition {

    private final Collection<BiomeType> biomes;

    public SpongeBiomeCondition(@Nonnull Collection<BiomeType> biomes) {
        this.biomes = biomes;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        Location<World> location = caught.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return biomes.contains(caught.getWorld().getBiome(x, y, z));
    }
}
