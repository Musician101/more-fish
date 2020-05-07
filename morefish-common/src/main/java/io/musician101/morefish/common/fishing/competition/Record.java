package io.musician101.morefish.common.fishing.competition;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class Record<A extends PlayerAnnouncement<?>, B, C extends FishCondition<?, ?>, H extends CatchHandler<?, ?>, I> implements Comparable<Record<A, B, C, H, I>> {

    @Nonnull
    private final Fish<A, B, C, H, I> fish;
    @Nonnull
    private final UUID fisher;

    public Record(@Nonnull UUID fisher, @Nonnull Fish<A, B, C, H, I> fish) {
        this.fisher = fisher;
        this.fish = fish;
    }

    public int compareTo(@Nonnull Record<A, B, C, H, I> other) {
        return Double.compare(fish.getLength(), other.fish.getLength());
    }

    @Nonnull
    public Fish<A, B, C, H, I> getFish() {
        return this.fish;
    }

    @Nonnull
    public UUID getFisher() {
        return this.fisher;
    }
}
