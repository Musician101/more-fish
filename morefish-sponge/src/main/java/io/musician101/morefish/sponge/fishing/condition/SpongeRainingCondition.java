package io.musician101.morefish.sponge.fishing.condition;

import javax.annotation.Nonnull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongeRainingCondition implements SpongeFishCondition {

    private final boolean raining;

    public SpongeRainingCondition(boolean raining) {
        this.raining = raining;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return caught.getWorld().getProperties().isRaining() == this.raining;
    }
}
