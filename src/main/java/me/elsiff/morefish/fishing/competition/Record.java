package me.elsiff.morefish.fishing.competition;

import java.util.UUID;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;

public final class Record implements Comparable<Record> {

    @Nonnull
    private final Fish fish;
    @Nonnull
    private final UUID fisher;

    public Record(@Nonnull UUID fisher, @Nonnull Fish fish) {
        this.fisher = fisher;
        this.fish = fish;
    }

    public int compareTo(@Nonnull Record other) {
        return Double.compare(fish.getLength(), other.fish.getLength());
    }

    @Nonnull
    public final Fish getFish() {
        return this.fish;
    }

    @Nonnull
    public final UUID getFisher() {
        return this.fisher;
    }
}
