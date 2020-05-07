package io.musician101.morefish.sponge.config.format;

import io.musician101.morefish.common.config.format.TextListFormat;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public final class SpongeTextListFormat extends TextListFormat<Player, SpongeTextListFormat, Text> implements SpongeFormat<SpongeTextListFormat, List<Text>> {

    public SpongeTextListFormat(@Nonnull List<String> strings) {
        super(strings);
    }

    @Nonnull
    public List<Text> output(@Nullable Player player) {
        return translated(strings);
    }
}
