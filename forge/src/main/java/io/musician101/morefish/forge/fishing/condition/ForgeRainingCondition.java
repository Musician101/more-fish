package io.musician101.morefish.forge.fishing.condition;

import java.util.UUID;
import javax.annotation.Nonnull;

public final class ForgeRainingCondition implements ForgeFishCondition {

    private final boolean raining;

    public ForgeRainingCondition(boolean raining) {
        this.raining = raining;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return caught.getEntityWorld().getWorldInfo().isRaining() == this.raining;
    }
}
