package io.musician101.morefish.forge.fishing.condition;

import java.util.UUID;
import javax.annotation.Nonnull;

public final class ForgeXPLevelCondition implements ForgeFishCondition {

    private final int minLevel;

    public ForgeXPLevelCondition(int minLevel) {
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return fisher.experienceLevel >= this.minLevel;
    }
}
