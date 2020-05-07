package io.musician101.morefish.sponge.config.format;

import io.musician101.morefish.common.config.format.Format;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public interface SpongeFormat<T extends SpongeFormat<T, R>, R> extends Format<Player, T, R> {

    @Nonnull
    default Text translated(@Nonnull String string) {
        return TextSerializers.FORMATTING_CODE.deserialize(string);
    }

    @Nonnull
    default List<Text> translated(@Nonnull List<String> strings) {
        return strings.stream().map(this::translated).collect(Collectors.toList());
    }
}
