package me.elsiff.morefish.fishing;

import javax.annotation.Nonnull;

public final class Fish {

    private final double length;
    @Nonnull
    private final FishType type;

    public Fish(@Nonnull FishType type, double length) {
        super();
        this.type = type;
        this.length = length;
    }

    public final double getLength() {
        return this.length;
    }

    @Nonnull
    public final FishType getType() {
        return this.type;
    }
}
