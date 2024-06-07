package me.elsiff.morefish.fish.catchhandler;

import me.elsiff.morefish.fish.Fish;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CatchHandler {

    void handle(@NotNull Player catcher, @NotNull Fish fish);
}
