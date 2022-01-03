package io.musician101.morefish.spigot.config.format;

import io.musician101.morefish.common.config.format.TextFormat;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.hooker.SpigotPlaceholderApiHooker;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SpigotTextFormat extends TextFormat<SpigotTextFormat, String> implements SpigotFormat<SpigotTextFormat, String> {

    public SpigotTextFormat(@Nonnull String string) {
        super(string);
    }

    @Nonnull
    public String output(@Nullable UUID player) {
        SpigotPlaceholderApiHooker placeholderApiHooker = SpigotMoreFish.getInstance().getPlaceholderApiHooker();
        if (placeholderApiHooker.hasHooked()) {
            return placeholderApiHooker.tryReplacing(string, player);
        }

        return string;
    }
}
