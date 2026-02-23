package me.elsiff.morefish.util;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@NullMarked
public interface EnumUtils {

    static <E extends Enum<E>, X extends Throwable> E getOrThrow(@Nullable String name, Class<E> clazz, X exception) throws X {
        return get(name, clazz).orElseThrow(() -> exception);
    }

    static <E extends Enum<E>> E get(@Nullable String name, Class<E> clazz, E defaultValue) {
        return get(e -> e.toString().equalsIgnoreCase(name), clazz, defaultValue);
    }

    static <E extends Enum<E>> Optional<E> get(@Nullable String name, Class<E> clazz) {
        return get(e -> e.toString().equalsIgnoreCase(name), clazz);
    }

    static <E extends Enum<E>> E get(Predicate<E> filter, Class<E> clazz, E defaultValue) {
        return values(clazz).flatMap(v -> Arrays.stream(v).filter(filter).findFirst()).orElse(defaultValue);
    }

    static <E extends Enum<E>> Optional<E> get(Predicate<E> filter, Class<E> clazz) {
        return values(clazz).flatMap(v -> Arrays.stream(v).filter(filter).findFirst());
    }

    static <E extends Enum<E>, X extends Throwable> E getOrThrow(Predicate<E> filter, Class<E> clazz, X exception) throws X {
        return get(filter, clazz).orElseThrow(() -> exception);
    }

    static <E extends Enum<E>> CompletableFuture<Suggestions> suggestions(SuggestionsBuilder builder, Class<E> clazz) {
        return suggestions(builder, (e, s) -> e.toString().toLowerCase().startsWith(s.toLowerCase()), clazz);
    }

    @SuppressWarnings("unchecked")
    static <E extends Enum<E>> Optional<E[]> values(Class<E> clazz) {
        try {
            Method method = clazz.getDeclaredMethod("values");
            return Optional.ofNullable((E[]) method.invoke(null));
        }
        catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    static <E extends Enum<E>> CompletableFuture<Suggestions> suggestions(SuggestionsBuilder builder, BiPredicate<E, String> filter, Class<E> clazz) {
        values(clazz).ifPresent(values -> Arrays.stream(values).filter(e -> filter.test(e, builder.getRemaining())).map(Enum::toString).forEach(builder::suggest));
        return builder.buildFuture();
    }
}
