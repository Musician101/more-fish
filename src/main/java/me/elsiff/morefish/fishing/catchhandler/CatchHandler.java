package me.elsiff.morefish.fishing.catchhandler;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.entity.Player;

public interface CatchHandler {

    void handle(@Nonnull Player var1, @Nonnull Fish var2);
}
