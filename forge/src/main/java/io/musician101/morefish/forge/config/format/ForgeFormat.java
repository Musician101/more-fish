package io.musician101.morefish.forge.config.format;

import io.musician101.morefish.common.config.format.Format;
import io.musician101.morefish.forge.util.TextParser;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public interface ForgeFormat<T extends ForgeFormat<T, R>, R> extends Format<T, R> {

    @Nonnull
    default Function<? super Object, ? extends String> translated(@Nonnull String string) {
        return new TextParser(string).getOutputAsString();
    }

    @Nonnull
    default List<String> translated(@Nonnull List<Object> strings) {
        return strings.stream().map(this::translated).collect(Collectors.toList());
    }
}
