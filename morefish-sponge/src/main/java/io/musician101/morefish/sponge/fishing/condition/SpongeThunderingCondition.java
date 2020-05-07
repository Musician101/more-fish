package io.musician101.morefish.sponge.fishing.condition;

import javax.annotation.Nonnull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongeThunderingCondition implements SpongeFishCondition {

    private final boolean thundering;

    public SpongeThunderingCondition(boolean thundering) {
        this.thundering = thundering;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return caught.getWorld().getProperties().isThundering() == this.thundering;
    }
}
