package me.elsiff.morefish.fishing.competition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.OfflinePlayer;

public final class Record implements Comparable<Record> {

    @Nonnull
    private final Fish fish;
    @Nonnull
    private final OfflinePlayer fisher;

    public Record(@Nonnull OfflinePlayer fisher, @Nonnull Fish fish) {
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
    public final OfflinePlayer getFisher() {
        return this.fisher;
    }
}
