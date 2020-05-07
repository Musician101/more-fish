package io.musician101.morefish.common.config;

import io.musician101.morefish.common.ConfigurateLoader;
import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.musicianlibrary.java.minecraft.common.config.AbstractConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;

public final class Config<F extends FishConfig<?, ?, ?, ?, ?>, L extends LangConfig<?, ?, ?>, M extends MessagesConfig<?, ?>, S extends FishShopConfig<?, ?, ?>, P extends Prize<?>> extends AbstractConfig {

    private static final int CONFIG_VERSION = 302;
    @Nonnull
    protected final AutoRunningConfig autoRunningConfig = new AutoRunningConfig();
    @Nonnull
    protected final F fishConfig;
    @Nonnull
    protected final S fishShopConfig;
    @Nonnull
    protected final L langConfig;
    @Nonnull
    protected final M messagesConfig;
    private final ConfigurateLoader loader;
    @Nonnull
    private final Function<List<String>, P> prizeMapper;
    @Nonnull
    private final Runnable saveDefaultFile;
    @Nonnull
    private final BiConsumer<Integer, Integer> versionChecker;
    protected boolean autoStart = false;
    @Nonnull
    protected List<String> disabledWorlds = new ArrayList<>();
    @Nonnull
    protected Map<Integer, Integer> fishBagUpgrades = new HashMap<>();
    @Nonnull
    protected String locale = "en";
    protected boolean noFishingUnlessContest = false;
    protected boolean onlyForContest = false;
    @Nonnull
    protected Map<Integer, P> prizes = new HashMap<>();
    protected boolean replaceOnlyFish = false;
    protected boolean saveRecords = false;
    protected boolean useBossBar = true;

    public Config(@Nonnull File configFile, @Nonnull Runnable saveDefaultFile, @Nonnull S fishShopConfig, @Nonnull M messagesConfig, @Nonnull F fishConfig, @Nonnull L langConfig, @Nonnull ConfigurateLoader loader, @Nonnull Function<List<String>, P> prizeMapper, @Nonnull BiConsumer<Integer, Integer> versionChecker) {
        super(configFile);
        this.saveDefaultFile = saveDefaultFile;
        this.fishShopConfig = fishShopConfig;
        this.messagesConfig = messagesConfig;
        this.loader = loader;
        this.prizeMapper = prizeMapper;
        this.versionChecker = versionChecker;
        this.fishConfig = fishConfig;
        this.langConfig = langConfig;
    }

    public final boolean autoStart() {
        return autoStart;
    }

    @Nonnull
    public final AutoRunningConfig getAutoRunningConfig() {
        return autoRunningConfig;
    }

    @Nonnull
    public final List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    @Nonnull
    public final Map<Integer, Integer> getFishBagUpgrades() {
        return fishBagUpgrades;
    }

    @Nonnull
    public final F getFishConfig() {
        return fishConfig;
    }

    @Nonnull
    public final S getFishShopConfig() {
        return fishShopConfig;
    }

    @Nonnull
    public final L getLangConfig() {
        return langConfig;
    }

    @Nonnull
    public final String getLocale() {
        return locale;
    }

    @Nonnull
    public final M getMessagesConfig() {
        return messagesConfig;
    }

    @Nonnull
    public final Map<Integer, P> getPrizes() {
        return prizes;
    }

    public final boolean noFishingUnlessContest() {
        return noFishingUnlessContest;
    }

    public final boolean onlyForContest() {
        return onlyForContest;
    }

    @Override
    public final void reload() {
        saveDefaultFile.run();
        try {
            ConfigurationNode config = loader.get(configFile.toPath()).load();
            ConfigurationNode general = config.getNode("general");
            if (!general.isVirtual()) {
                locale = general.getNode("locale").getString("en");
                autoStart = general.getNode("auto-start").getBoolean(false);
                useBossBar = general.getNode("use-boss-bar").getBoolean(true);
                onlyForContest = general.getNode("only-for-contest").getBoolean(false);
                noFishingUnlessContest = general.getNode("no-fishing-unless-contest").getBoolean(false);
                disabledWorlds = general.getNode("contest-disabled-worlds").getList(Objects::toString);
                replaceOnlyFish = general.getNode("replace-only-fish").getBoolean(false);
                saveRecords = general.getNode("save-records").getBoolean(false);
            }

            ConfigurationNode upgrades = config.getNode("fish-bag-upgrades");
            if (!upgrades.isVirtual()) {
                fishBagUpgrades = upgrades.getChildrenMap().entrySet().stream().collect(Collectors.toMap(e -> (int) e.getKey(), e -> e.getValue().getInt()));
            }

            ConfigurationNode prizes = config.getNode("contest-prizes");
            if (!prizes.isVirtual()) {
                this.prizes = prizes.getChildrenMap().entrySet().stream().collect(Collectors.toMap(e -> ((int) e.getKey()) - 1, e -> prizeMapper.apply(e.getValue().getList(Object::toString))));
            }

            autoRunningConfig.load(config.getNode("auto-running"));
            fishShopConfig.load(config.getNode("fish-shop"));
            messagesConfig.load(config.getNode("messages"));
            versionChecker.accept(config.getNode("version").getInt(0), CONFIG_VERSION);
            fishConfig.reload();
            langConfig.reload();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final boolean replaceOnlyFish() {
        return replaceOnlyFish;
    }

    public final boolean saveRecords() {
        return saveRecords;
    }

    public final boolean useBossBar() {
        return useBossBar;
    }
}
