package io.musician101.morefish.common.config;

import io.musician101.morefish.common.config.format.TextFormat;
import io.musician101.morefish.common.config.format.TextListFormat;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.ConfigurationNode;

@SuppressWarnings("unused")
public record LangConfig<F extends TextFormat<F, T>, L extends TextListFormat<L, T>, T>(@Nonnull ConfigurationNode lang,
                                                                                        @Nonnull Function<ConfigurationNode, F> format,
                                                                                        @Nonnull Function<List<? extends ConfigurationNode>, L> listFormat,
                                                                                        @Nonnull Function<ConfigurationNode, T> translated,
                                                                                        @Nonnull Function<Long, T> time) {

    private static final int CONFIG_VERSION = 211;

    public static <F extends TextFormat<F, T>, L extends TextListFormat<L, T>, T> LangConfig<F, L, T> deserialize(@Nonnull ConfigurationNode node, @Nonnull Function<ConfigurationNode, F> format, @Nonnull Function<List<? extends ConfigurationNode>, L> listFormat, @Nonnull Function<ConfigurationNode, T> translated, @Nonnull Function<Long, T> time, @Nonnull BiConsumer<Integer, Integer> versionChecker) {
        versionChecker.accept(node.node("version").getInt(0), CONFIG_VERSION);
        return new LangConfig<>(node, format, listFormat, translated, time);
    }

    @Nonnull
    public F format(@Nonnull String id) {
        return format.apply(lang.node(id));
    }

    @Nonnull
    public L formats(@Nonnull String id) {
        return listFormat.apply(lang.node(id).childrenList());
    }

    @Nonnull
    public T text(@Nonnull String id) {
        return translated(lang.node(id));
    }

    @Nonnull
    public List<T> texts(@Nonnull String id) {
        return translated(lang.node(id).childrenList());
    }

    @Nonnull
    public T time(long second) {
        return time.apply(second);
    }

    @Nonnull
    private List<T> translated(@Nonnull List<? extends ConfigurationNode> strings) {
        return strings.stream().map(this::translated).collect(Collectors.toList());
    }

    @Nonnull
    private T translated(@Nonnull ConfigurationNode string) {
        return translated.apply(string);
    }
}
