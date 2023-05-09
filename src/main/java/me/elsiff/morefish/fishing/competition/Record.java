package me.elsiff.morefish.fishing.competition;

import java.util.UUID;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;

public record Record(@Nonnull UUID fisher, @Nonnull Fish fish) implements Comparable<Record> {

    public int compareTo(@Nonnull Record other) {
        return Double.compare(fish.length(), other.fish.length());
    }
}
