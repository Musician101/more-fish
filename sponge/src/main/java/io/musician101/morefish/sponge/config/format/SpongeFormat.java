package io.musician101.morefish.sponge.config.format;

import io.musician101.morefish.common.config.format.Format;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public interface SpongeFormat<T extends SpongeFormat<T, R>, R> extends Format<T, R> {

    @Nonnull
    default Component translated(@Nonnull String string) {
        return GsonComponentSerializer.builder().build().deserialize(string);
    }

    @Nonnull
    default List<Component> translated(@Nonnull List<String> strings) {
        return strings.stream().map(this::translated).collect(Collectors.toList());
    }
}
