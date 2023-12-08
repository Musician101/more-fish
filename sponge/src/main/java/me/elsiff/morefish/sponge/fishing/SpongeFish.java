package me.elsiff.morefish.sponge.fishing;

import me.elsiff.morefish.common.fishing.Fish;
import org.jetbrains.annotations.NotNull;

public class SpongeFish extends Fish<SpongeFishType> {

    public SpongeFish(@NotNull SpongeFishType type, double length) {
        super(type, length);
    }
}
