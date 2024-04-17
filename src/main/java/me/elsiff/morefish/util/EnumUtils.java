package me.elsiff.morefish.util;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface EnumUtils {

    @NotNull
    static <E extends Enum<E>, X extends Throwable> E getOrThrow(@NotNull String name, Class<E> clazz, X exception) throws X {
        return get(name, clazz).orElseThrow(() -> exception);
    }

    @NotNull
    static <E extends Enum<E>> Optional<E> get(@NotNull String name, Class<E> clazz) {
        return get(e -> name.equalsIgnoreCase(e.toString()), clazz);
    }

    @NotNull
    static <E extends Enum<E>> Optional<E> get(@NotNull Predicate<E> filter, Class<E> clazz) {
        return values(clazz).flatMap(v -> Arrays.stream(v).filter(filter).findFirst());
    }

    @NotNull
    static <E extends Enum<E>> CompletableFuture<Suggestions> suggestions(@NotNull SuggestionsBuilder builder, @NotNull Class<E> clazz) {
        return suggestions(builder, (e, s) -> e.toString().toLowerCase().startsWith(s.toLowerCase()), clazz);
    }

    @SuppressWarnings("unchecked")
    static <E extends Enum<E>> Optional<E[]> values(@NotNull Class<E> clazz) {
        try {
            Method method = clazz.getDeclaredMethod("values");
            return Optional.ofNullable((E[]) method.invoke(null));
        }
        catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    @NotNull
    static <E extends Enum<E>> CompletableFuture<Suggestions> suggestions(@NotNull SuggestionsBuilder builder, @NotNull BiPredicate<E, String> filter, @NotNull Class<E> clazz) {
        values(clazz).ifPresent(values -> Arrays.stream(values).filter(e -> filter.test(e, builder.getRemaining())).map(Enum::toString).forEach(builder::suggest));
        return builder.buildFuture();
    }
}
