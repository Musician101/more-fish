package io.musician101.morefish.common.config.format;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public abstract class TextListFormat<T extends TextListFormat<T, R>, R> implements Format<T, List<R>> {

    @Nonnull
    protected List<String> strings;

    public TextListFormat(@Nonnull List<String> strings) {
        this.strings = strings;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public final T replace(@Nonnull Collection<Entry<String, Object>> pairs) {
        pairs.forEach(pair -> strings = strings.stream().map(string -> string.replace(pair.getKey(), pair.getValue().toString())).collect(Collectors.toList()));
        return (T) this;
    }
}
