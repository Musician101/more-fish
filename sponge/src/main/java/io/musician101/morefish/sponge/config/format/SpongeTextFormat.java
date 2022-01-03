package io.musician101.morefish.sponge.config.format;

import io.musician101.morefish.common.config.format.TextFormat;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;

public final class SpongeTextFormat extends TextFormat<SpongeTextFormat, Component> implements SpongeFormat<SpongeTextFormat, Component> {

    public SpongeTextFormat(@Nonnull String string) {
        super(string);
    }

    @Nonnull
    public Component output(@Nullable UUID player) {
        return Component.text(string);
    }
}
