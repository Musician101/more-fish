package me.elsiff.morefish.fishing.competition;

import java.util.UUID;
import me.elsiff.morefish.fishing.Fish;
import org.jetbrains.annotations.NotNull;

public record Record(@NotNull UUID fisher, @NotNull Fish fish) implements Comparable<Record> {

    public int compareTo(@NotNull Record other) {
        return Double.compare(fish.length(), other.fish.length());
    }
}
