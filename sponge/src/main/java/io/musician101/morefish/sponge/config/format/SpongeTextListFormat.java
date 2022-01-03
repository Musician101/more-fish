package io.musician101.morefish.sponge.config.format;

import io.musician101.morefish.common.config.format.TextListFormat;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;

public final class SpongeTextListFormat extends TextListFormat<SpongeTextListFormat, Component> implements SpongeFormat<SpongeTextListFormat, List<Component>> {

    public SpongeTextListFormat(@Nonnull List<String> strings) {
        super(strings);
    }

    @Nonnull
    public List<Component> output(@Nullable UUID player) {
        return translated(strings);
    }
}
