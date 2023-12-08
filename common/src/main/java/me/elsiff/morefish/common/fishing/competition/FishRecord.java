package me.elsiff.morefish.common.fishing.competition;

import java.util.UUID;
import me.elsiff.morefish.common.fishing.Fish;
import org.jetbrains.annotations.NotNull;

public record FishRecord<F extends Fish<?>>(@NotNull UUID fisher,
                                            @NotNull F fish) implements Comparable<FishRecord<F>> {

    public int compareTo(@NotNull FishRecord other) {
        return Double.compare(fish.length(), other.fish.length());
    }
}
