package io.musician101.morefish.common.fishing;

import javax.annotation.Nonnull;

public record Fish(@Nonnull FishType type, double length) {

    public double getLength() {
        return this.length;
    }

    @Nonnull
    public FishType getType() {
        return this.type;
    }
}
