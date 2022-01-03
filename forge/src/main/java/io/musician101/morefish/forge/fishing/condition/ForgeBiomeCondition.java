package io.musician101.morefish.forge.fishing.condition;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.world.biome.Biome;

public final class ForgeBiomeCondition implements ForgeFishCondition {

    private final Collection<Biome> biomes;

    public ForgeBiomeCondition(@Nonnull Collection<Biome> biomes) {
        this.biomes = biomes;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return biomes.contains(caught.getEntityWorld().func_225523_d_().func_226836_a_(caught.getPosition()));
    }
}
