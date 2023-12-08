package me.elsiff.morefish.paper.fishing;

import me.elsiff.morefish.common.fishing.Fish;
import org.jetbrains.annotations.NotNull;

public class PaperFish extends Fish<PaperFishType> {

    public PaperFish(@NotNull PaperFishType type, double length) {
        super(type, length);
    }
}
