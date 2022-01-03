package io.musician101.morefish.forge.fishing.condition;

import io.musician101.morefish.forge.util.NumberUtils.DoubleRange;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class ForgeLocationYCondition implements ForgeFishCondition {

    private final DoubleRange range;

    public ForgeLocationYCondition(@Nonnull DoubleRange range) {
        this.range = range;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return range.containsDouble(fisher.getPositionVec().getY());
    }
}
