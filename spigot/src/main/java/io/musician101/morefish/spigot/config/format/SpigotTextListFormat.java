package io.musician101.morefish.spigot.config.format;

import io.musician101.morefish.common.config.format.TextListFormat;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.hooker.SpigotPlaceholderApiHooker;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SpigotTextListFormat extends TextListFormat<SpigotTextListFormat, String> implements SpigotFormat<SpigotTextListFormat, List<String>> {

    public SpigotTextListFormat(@Nonnull List<String> strings) {
        super(strings);
    }

    @Nonnull
    public List<String> output(@Nullable UUID player) {
        return translated(strings.stream().map(string -> {
            SpigotPlaceholderApiHooker placeholderApiHooker = SpigotMoreFish.getInstance().getPlaceholderApiHooker();
            if (placeholderApiHooker.hasHooked()) {
                return placeholderApiHooker.tryReplacing(string, player);
            }

            return string;
        }).collect(Collectors.toList()));
    }
}
