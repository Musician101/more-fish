package io.musician101.morefish.common.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import javax.annotation.Nonnull;

public interface CatchHandler<F extends Fish<?, ?, ?, ?, ?>, P> {

    void handle(@Nonnull P catcher, @Nonnull F fish);
}
