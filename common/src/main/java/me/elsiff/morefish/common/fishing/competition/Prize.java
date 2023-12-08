package me.elsiff.morefish.common.fishing.competition;

import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public abstract class Prize {

    protected final List<String> commands;

    public Prize(@NotNull List<String> commands) {
        this.commands = commands;
    }

    public abstract void giveTo(@NotNull UUID player, int rankNumber);
}
