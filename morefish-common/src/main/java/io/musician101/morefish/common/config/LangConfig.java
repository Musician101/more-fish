package io.musician101.morefish.common.config;

import io.musician101.morefish.common.ConfigurateLoader;
import io.musician101.morefish.common.config.format.TextFormat;
import io.musician101.morefish.common.config.format.TextListFormat;
import io.musician101.musicianlibrary.java.minecraft.common.config.AbstractConfig;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;

public final class LangConfig<F extends TextFormat<?, ?, ?>, L extends TextListFormat<?, ?, ?>, T> extends AbstractConfig {

    private static final int CONFIG_VERSION = 211;
    @Nonnull
    private final Function<String, F> format;
    @Nonnull
    private final Function<List<String>, L> listFormat;
    @Nonnull
    private final ConfigurateLoader loader;
    @Nonnull
    private final Supplier<File> saveDefaultFile;
    @Nonnull
    private final Function<Long, T> time;
    @Nonnull
    private final Function<String, T> translated;
    @Nonnull
    private final BiConsumer<Integer, Integer> versionChecker;
    @Nonnull
    protected ConfigurationNode lang;

    public LangConfig(@Nonnull File configFile, @Nonnull Supplier<File> saveDefaultFile, @Nonnull ConfigurationNode lang, @Nonnull ConfigurateLoader loader, @Nonnull Function<String, F> format, @Nonnull Function<List<String>, L> listFormat, @Nonnull Function<String, T> translated, @Nonnull Function<Long, T> time, @Nonnull BiConsumer<Integer, Integer> versionChecker) {
        super(configFile);
        this.saveDefaultFile = saveDefaultFile;
        this.lang = lang;
        this.loader = loader;
        this.format = format;
        this.listFormat = listFormat;
        this.translated = translated;
        this.time = time;
        this.versionChecker = versionChecker;
    }

    @Nonnull
    public F format(@Nonnull String id) {
        return format.apply(lang.getNode(id).getString());
    }

    @Nonnull
    public L formats(@Nonnull String id) {
        return listFormat.apply(lang.getNode(id).getList(Objects::toString));
    }

    @Override
    public final void reload() {
        configFile = saveDefaultFile.get();
        try {
            lang = loader.get(configFile.toPath()).load();
            versionChecker.accept(lang.getNode("version").getInt(0), CONFIG_VERSION);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    public T text(@Nonnull String id) {
        return translated(lang.getNode(id).getString());
    }

    @Nonnull
    public List<T> texts(@Nonnull String id) {
        return translated(lang.getNode(id).getList(Objects::toString));
    }

    @Nonnull
    public T time(long second) {
        return time.apply(second);
    }

    @Nonnull
    protected final List<T> translated(@Nonnull List<String> strings) {
        return strings.stream().map(this::translated).collect(Collectors.toList());
    }

    @Nonnull
    protected T translated(@Nonnull String string) {
        return translated.apply(string);
    }
}
