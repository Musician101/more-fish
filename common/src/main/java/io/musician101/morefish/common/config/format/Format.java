package io.musician101.morefish.common.config.format;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Format<T, R> {

    @Nonnull
    R output(@Nullable UUID player);

    @Nonnull
    default R output() {
        return output(null);
    }

    @Nonnull
    default T replace(@Nonnull Map<String, Object> pairs) {
        return replace(pairs.entrySet());
    }

    @Nonnull
    T replace(@Nonnull Collection<Entry<String, Object>> pairs);
}
