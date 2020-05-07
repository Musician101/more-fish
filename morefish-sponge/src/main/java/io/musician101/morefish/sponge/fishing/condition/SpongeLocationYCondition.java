package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.sponge.util.NumberUtils.DoubleRange;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongeLocationYCondition implements SpongeFishCondition {

    private final DoubleRange range;

    public SpongeLocationYCondition(@Nonnull DoubleRange range) {
        this.range = range;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return range.containsDouble(fisher.getLocation().getY());
    }
}
