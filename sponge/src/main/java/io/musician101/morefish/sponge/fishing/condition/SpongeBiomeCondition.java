package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.biome.Biome;
import org.spongepowered.api.world.server.ServerLocation;

public final class SpongeBiomeCondition implements FishCondition {

    private final Collection<Biome> biomes;

    public SpongeBiomeCondition(@Nonnull Collection<Biome> biomes) {
        this.biomes = biomes;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return Sponge.server().worldManager().worlds().stream().map(world -> world.entity(caught)).filter(Optional::isPresent).map(Optional::get).findFirst().map(entity -> {
            ServerLocation location = (ServerLocation) entity.location();
            int x = location.blockX();
            int y = location.blockY();
            int z = location.blockZ();
            return biomes.contains(entity.world().biome(x, y, z));
        }).orElse(false);
    }
}
