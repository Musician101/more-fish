package me.elsiff.morefish.common.fishing.catchhandler;

import me.elsiff.morefish.common.fishing.Fish;
import org.jetbrains.annotations.NotNull;

public interface CatchHandler<F extends Fish<?>, P> {

    void handle(@NotNull P catcher, @NotNull F fish);
}
