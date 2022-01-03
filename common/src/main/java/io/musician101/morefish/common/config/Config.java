package io.musician101.morefish.common.config;

import io.musician101.morefish.common.config.format.TextFormat;
import io.musician101.morefish.common.config.format.TextListFormat;
import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public final class Config<F extends TextFormat<F, T>, L extends TextListFormat<L, T>, T> {

    private static final int CONFIG_VERSION = 302;
    @Nonnull
    private final AutoRunningConfig autoRunningConfig = new AutoRunningConfig();
    @Nonnull
    private final File configFile;
    @Nonnull
    private final Function<ConfigurationNode, F> format;
    private final Function<List<? extends ConfigurationNode>, L> listFormat;
    private final ConfigurateLoader loader;
    @Nonnull
    private final Runnable saveDefaultConfig;
    @Nonnull
    private final Runnable saveDefaultLocales;
    @Nonnull
    private final Function<Long, T> time;
    @Nonnull
    private final Function<ConfigurationNode, T> translated;
    @Nonnull
    private final TypeSerializerCollection typeSerializerCollection;
    @Nonnull
    private final BiConsumer<Integer, Integer> versionChecker;
    private boolean autoStart = false;
    @Nonnull
    private List<String> disabledWorlds = new ArrayList<>();
    @Nonnull
    private Map<Integer, Integer> fishBagUpgrades = new HashMap<>();
    private FishConfig fishConfig;
    private FishShopConfig fishShopConfig;
    private LangConfig<F, L, T> langConfig;
    @Nonnull
    private String locale = "en";
    private MessagesConfig messagesConfig;
    private boolean noFishingUnlessContest = false;
    private boolean onlyForContest = false;
    @Nonnull
    private Map<Integer, Prize> prizes = new HashMap<>();
    private boolean replaceOnlyFish = false;
    private boolean saveRecords = false;
    private boolean useBossBar = true;

    public Config(@Nonnull File configFile, @Nonnull ConfigurateLoader loader, @Nonnull TypeSerializerCollection typeSerializerCollection, @Nonnull Runnable saveDefaultConfig, @Nonnull Runnable saveDefaultLocales, @Nonnull BiConsumer<Integer, Integer> versionChecker, @Nonnull Function<ConfigurationNode, F> format, @Nonnull Function<List<? extends ConfigurationNode>, L> listFormat, @Nonnull Function<Long, T> time, @Nonnull Function<ConfigurationNode, T> translated) {
        this.configFile = configFile;
        this.loader = loader;
        this.typeSerializerCollection = typeSerializerCollection;
        this.saveDefaultConfig = saveDefaultConfig;
        this.saveDefaultLocales = saveDefaultLocales;
        this.versionChecker = versionChecker;
        this.format = format;
        this.listFormat = listFormat;
        this.time = time;
        this.translated = translated;
    }

    public boolean autoStart() {
        return autoStart;
    }

    @Nonnull
    public AutoRunningConfig getAutoRunningConfig() {
        return autoRunningConfig;
    }

    @Nonnull
    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    @Nonnull
    public Map<Integer, Integer> getFishBagUpgrades() {
        return fishBagUpgrades;
    }

    @Nonnull
    public FishConfig getFishConfig() {
        return fishConfig;
    }

    @Nonnull
    public FishShopConfig getFishShopConfig() {
        return fishShopConfig;
    }

    @Nonnull
    public LangConfig<F, L, T> getLangConfig() {
        return langConfig;
    }

    @Nonnull
    public String getLocale() {
        return locale;
    }

    @Nonnull
    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    @Nonnull
    public Map<Integer, Prize> getPrizes() {
        return prizes;
    }

    public boolean noFishingUnlessContest() {
        return noFishingUnlessContest;
    }

    public boolean onlyForContest() {
        return onlyForContest;
    }

    public void reload() throws ConfigurateException {
        saveDefaultConfig.run();
        ConfigurationNode config = loader.loader(configFile.toPath(), typeSerializerCollection).load();
        ConfigurationNode general = config.node("general");
        if (!general.empty()) {
            locale = general.node("locale").getString("en");
            autoStart = general.node("auto-start").getBoolean(false);
            useBossBar = general.node("use-boss-bar").getBoolean(true);
            onlyForContest = general.node("only-for-contest").getBoolean(false);
            noFishingUnlessContest = general.node("no-fishing-unless-contest").getBoolean(false);
            disabledWorlds = general.node("contest-disabled-worlds").getList(String.class, new ArrayList<>());
            replaceOnlyFish = general.node("replace-only-fish").getBoolean(false);
            saveRecords = general.node("save-records").getBoolean(false);
        }

        ConfigurationNode upgrades = config.node("fish-bag-upgrades");
        if (!upgrades.empty()) {
            fishBagUpgrades = upgrades.childrenMap().entrySet().stream().collect(Collectors.toMap(e -> (int) e.getKey(), e -> e.getValue().getInt()));
        }

        ConfigurationNode prizes = config.node("contest-prizes");
        if (!prizes.empty()) {
            Map<Integer, Prize> map = new HashMap<>();
            for (Entry<Object, ? extends ConfigurationNode> e : prizes.childrenMap().entrySet()) {
                map.put(((int) e.getKey()) - 1, e.getValue().get(Prize.class));
            }

            this.prizes = map;
        }

        autoRunningConfig.load(config.node("auto-running"));
        fishShopConfig = FishShopConfig.deserialize(config.node("fish-shop"));
        messagesConfig = config.node("messages").get(MessagesConfig.class);
        versionChecker.accept(config.node("version").getInt(0), CONFIG_VERSION);
        File configDir = configFile.getParentFile();
        File localeDir = new File(configDir, "locale");
        String path = configFile.getPath();
        String extension = path.substring(path.lastIndexOf('.') - 1);
        saveDefaultLocales.run();
        fishConfig = FishConfig.deserialize(loader.loader(new File(localeDir, "fish_" + locale + extension).toPath(), typeSerializerCollection).load(), versionChecker, messagesConfig.getAnnounceCatch());
        langConfig = LangConfig.deserialize(loader.loader(new File(localeDir, "lang_" + locale + extension).toPath(), typeSerializerCollection).load(), format, listFormat, translated, time, versionChecker);
    }

    public boolean replaceOnlyFish() {
        return replaceOnlyFish;
    }

    public boolean saveRecords() {
        return saveRecords;
    }

    public boolean useBossBar() {
        return useBossBar;
    }
}
