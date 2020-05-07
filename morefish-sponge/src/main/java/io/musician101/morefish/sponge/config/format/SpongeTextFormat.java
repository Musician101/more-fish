package io.musician101.morefish.sponge.config.format;

import io.musician101.morefish.common.config.format.TextFormat;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public final class SpongeTextFormat extends TextFormat<Player, SpongeTextFormat, Text> implements SpongeFormat<SpongeTextFormat, Text> {

    public SpongeTextFormat(@Nonnull String string) {
        super(string);
    }

    @Nonnull
    public Text output(@Nullable Player player) {
        return Text.of(string);
    }
}
