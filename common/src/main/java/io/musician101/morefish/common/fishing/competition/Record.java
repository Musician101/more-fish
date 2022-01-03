package io.musician101.morefish.common.fishing.competition;

import io.musician101.morefish.common.fishing.Fish;
import java.util.UUID;
import javax.annotation.Nonnull;

public record Record(@Nonnull UUID fisher, @Nonnull Fish fish) implements Comparable<Record> {

    public Record(@Nonnull UUID fisher, @Nonnull Fish fish) {
        this.fisher = fisher;
        this.fish = fish;
    }

    public int compareTo(@Nonnull Record other) {
        return Double.compare(fish.getLength(), other.fish.getLength());
    }

    @Nonnull
    public Fish getFish() {
        return this.fish;
    }

    @Nonnull
    public UUID getFisher() {
        return this.fisher;
    }
}
