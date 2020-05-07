package io.musician101.morefish.common.fishing.competition;

import java.util.List;
import javax.annotation.Nonnull;

public abstract class Prize<U> {

    @Nonnull
    protected final List<String> commands;

    protected Prize(@Nonnull List<String> commands) {
        this.commands = commands;
    }

    public abstract void giveTo(@Nonnull U user, int rankNumber);
}
