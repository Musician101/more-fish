package io.musician101.morefish.forge.fishing.condition;

import java.util.UUID;
import javax.annotation.Nonnull;

public final class ForgeThunderingCondition implements ForgeFishCondition {

    private final boolean thundering;

    public ForgeThunderingCondition(boolean thundering) {
        this.thundering = thundering;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return caught.getEntityWorld().isThundering() == this.thundering;
    }
}
