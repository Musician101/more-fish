package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import io.musician101.morefish.sponge.util.NumberUtils.DoubleRange;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;

public final class SpongeLocationYCondition implements FishCondition {

    private final DoubleRange range;

    public SpongeLocationYCondition(@Nonnull DoubleRange range) {
        this.range = range;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return range.containsDouble(Sponge.server().player(fisher).get().location().y());
    }
}
