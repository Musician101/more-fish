package me.elsiff.morefish.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@FunctionalInterface
@NullMarked
public interface CheckedConsumer<V, E extends Throwable> {

    void accept(V value) throws E;

    @SuppressWarnings("unchecked")
    static <E extends Exception, V> Function<V, @Nullable E> asFunction(CheckedConsumer<V, E> consumer) {
        return v -> {
            try {
                consumer.accept(v);
                return null;
            }
            catch (Exception e) {
                return (E) e;
            }
        };
    }
}
