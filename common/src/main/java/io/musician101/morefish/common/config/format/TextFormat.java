package io.musician101.morefish.common.config.format;

import java.util.Collection;
import java.util.Map.Entry;
import javax.annotation.Nonnull;

public abstract class TextFormat<T extends TextFormat<T, R>, R> implements Format<T, R> {

    @Nonnull
    protected String string;

    public TextFormat(@Nonnull String string) {
        this.string = string;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public T replace(@Nonnull Collection<Entry<String, Object>> pairs) {
        pairs.forEach(pair -> string = string.replace(pair.getKey(), pair.getValue().toString()));
        return (T) this;
    }
}
