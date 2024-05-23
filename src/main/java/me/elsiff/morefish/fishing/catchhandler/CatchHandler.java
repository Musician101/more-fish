package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CatchHandler {

    void handle(@NotNull Player catcher, @NotNull Fish fish);
}
