package io.musician101.morefish.common.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface CatchHandler {

    void handle(@Nonnull UUID catcherID, @Nonnull Fish fish);
}
