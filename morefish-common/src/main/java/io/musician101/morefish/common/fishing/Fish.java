package io.musician101.morefish.common.fishing;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import javax.annotation.Nonnull;

public final class Fish<A extends PlayerAnnouncement<?>, B, C extends FishCondition<?, ?>, H extends CatchHandler<?, ?>, I> {

    private final double length;
    @Nonnull
    private final FishType<A, B, C, H, I> type;

    public Fish(@Nonnull FishType<A, B, C, H, I> type, double length) {
        this.type = type;
        this.length = length;
    }

    public double getLength() {
        return this.length;
    }

    @Nonnull
    public FishType<A, B, C, H, I> getType() {
        return this.type;
    }
}
