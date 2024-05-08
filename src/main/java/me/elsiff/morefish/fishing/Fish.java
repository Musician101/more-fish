package me.elsiff.morefish.fishing;

import org.jetbrains.annotations.NotNull;

public record Fish(@NotNull FishType type, double length) {

    @NotNull
    public FishRarity rarity() {
        return type.rarity();
    }

    @NotNull
    public String name() {
        return type.name();
    }
}
