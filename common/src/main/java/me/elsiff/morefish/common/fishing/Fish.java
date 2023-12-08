package me.elsiff.morefish.common.fishing;

import org.jetbrains.annotations.NotNull;

public class Fish<T extends FishType<?, ?, ?, ?, ?>> {

    private final double length;
    private final @NotNull T type;

    public Fish(@NotNull T type, double length) {
        this.type = type;
        this.length = length;
    }

    public double length() {
        return length;
    }

    public @NotNull T type() {
        return type;
    }
}
