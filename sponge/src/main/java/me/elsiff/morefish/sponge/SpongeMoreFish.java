package me.elsiff.morefish.sponge;

import com.google.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import me.elsiff.morefish.common.MoreFish;
import me.elsiff.morefish.common.configuration.Lang;
import me.elsiff.morefish.sponge.command.MFMain;
import me.elsiff.morefish.sponge.configuration.Config;
import me.elsiff.morefish.sponge.fishing.FishingListener;
import me.elsiff.morefish.sponge.fishing.SpongeFishBags;
import me.elsiff.morefish.sponge.fishing.SpongeFishTypeTable;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetitionAutoRunner;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetitionHost;
import me.elsiff.morefish.sponge.hooker.EconomyHooker;
import me.elsiff.morefish.sponge.hooker.ScoreboardHooker;
import me.elsiff.morefish.sponge.item.FishItemStackConverter;
import me.elsiff.morefish.sponge.shop.FishShopSignListener;
import me.elsiff.morefish.sponge.shop.SpongeFishShop;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.persistence.DataStore;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.RegisterDataEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import static me.elsiff.morefish.common.configuration.Lang.SHOP_GUI_TITLE;
import static me.elsiff.morefish.common.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;

@Plugin(SpongeMoreFish.ID)
public final class SpongeMoreFish implements MoreFish<SpongeFishBags, SpongeFishingCompetition, SpongeFishingCompetitionHost, SpongeFishTypeTable> {

    static final String ID = "more-fish";

    @NotNull private final SpongeFishingCompetitionAutoRunner autoRunner = new SpongeFishingCompetitionAutoRunner();
    @NotNull private final SpongeFishingCompetition competition = new SpongeFishingCompetition();
    @NotNull private final SpongeFishingCompetitionHost competitionHost = new SpongeFishingCompetitionHost();
    private final Path configDir;
    @NotNull private final EconomyHooker economyHooker = new EconomyHooker();
    @NotNull private final SpongeFishBags fishBags = new SpongeFishBags();
    @NotNull private final SpongeFishShop fishShop = new SpongeFishShop();
    @NotNull private final SpongeFishTypeTable fishTypeTable = new SpongeFishTypeTable();
    @NotNull private final ScoreboardHooker musiBoard = new ScoreboardHooker();
    private final PluginContainer pluginContainer;
    @NotNull private ConfigurationNode config = BasicConfigurationNode.root();

    @Inject
    public SpongeMoreFish(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDir) {
        this.pluginContainer = pluginContainer;
        this.configDir = configDir;
    }

    public static SpongeMoreFish getPlugin() {
        return Sponge.pluginManager().plugin(ID).map(PluginContainer::instance).filter(SpongeMoreFish.class::isInstance).map(SpongeMoreFish.class::cast).orElseThrow(() -> new IllegalStateException("MoreFish is not enabled!"));
    }

    public void applyConfig() {
        Sponge.server().onlinePlayers().forEach(player -> player.openInventory().filter(InventoryMenu.class::isInstance).map(InventoryMenu.class::cast).flatMap(InventoryMenu::title).filter(title -> title.equals(SHOP_GUI_TITLE) || title.equals(text("Set Sale Filter(s)"))).ifPresent(title -> {
            player.closeInventory();
            player.sendMessage(join(Lang.PREFIX, text(" The config is being updated. To prevent issues, the window has been closed.")));
        }));

        if (Files.notExists(configDir)) {
            try {
                Files.createDirectories(configDir);
            }
            catch (IOException e) {
                getLogger().error("Failed to write default config!", e);
            }
        }

        Path configFile = configDir.resolve("config.yml");
        if (Files.notExists(configFile)) {
            pluginContainer.openResource(URI.create("config.yml")).ifPresent(i -> {
                try {
                    Files.createFile(configFile);
                    Files.write(configFile, i.readAllBytes());
                }
                catch (IOException e) {
                    getLogger().error("Failed to write default config!", e);
                }
            });
        }

        try {
            this.config = YamlConfigurationLoader.builder().file(configFile.toFile()).build().load();
        }
        catch (ConfigurateException e) {
            getLogger().error("Failed to read config!", e);
        }

        fishTypeTable.load();
        getLogger().info("Loaded " + fishTypeTable.getRarities().size() + " rarities and " + fishTypeTable.getTypes().size() + " fish types");
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        if (getConfig().node("auto-running.enable").getBoolean()) {
            List<LocalTime> scheduledTimes = Config.getScheduledTimes();
            autoRunner.setScheduledTimes(scheduledTimes);
            autoRunner.enable();
        }
    }

    @NotNull
    @Override
    public SpongeFishingCompetition getCompetition() {
        return competition;
    }

    @NotNull
    @Override
    public SpongeFishingCompetitionHost getCompetitionHost() {
        return competitionHost;
    }

    public @NotNull ConfigurationNode getConfig() {
        return config;
    }

    public Path getConfigDir() {
        return configDir;
    }

    private <V> DataRegistration getData(Key<Value<V>> key, BiConsumer<DataView, V> serializer, Function<DataView, Optional<V>> deserializer) {
        return DataRegistration.builder().dataKey(key).store(DataStore.builder().pluginData(key.key()).holder(ItemStack.class).key(key, serializer, deserializer).build()).build();
    }

    @NotNull
    public EconomyHooker getEconomyHooker() {
        return economyHooker;
    }

    @NotNull
    @Override
    public SpongeFishBags getFishBags() {
        return fishBags;
    }

    @NotNull
    @Override
    public SpongeFishShop getFishShop() {
        return fishShop;
    }

    @NotNull
    @Override
    public SpongeFishTypeTable getFishTypeTable() {
        return fishTypeTable;
    }

    public Logger getLogger() {
        return pluginContainer.logger();
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @NotNull
    public ScoreboardHooker getScoreboardHooker() {
        return musiBoard;
    }

    @Listener
    public void onServerStart(StartingEngineEvent<Server> event) {
        economyHooker.hookIfEnabled();
        musiBoard.hookIfEnabled();
        fishBags.load();
        applyConfig();
        EventManager em = Sponge.eventManager();
        em.registerListeners(pluginContainer, new FishingListener());
        em.registerListeners(pluginContainer, new FishShopSignListener());
        em.registerListeners(pluginContainer, fishBags);
        getLogger().info("Plugin has been enabled.");
        if (getConfig().node("general.auto-start").getBoolean()) {
            competitionHost.openCompetition();
        }
    }

    @Listener
    public void onServerStop(StoppingEngineEvent<Server> event) {
        fishBags.save();
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        getLogger().info("Plugin has been disabled.");
    }

    @Listener
    public void registerCommands(RegisterCommandEvent<Command> event) {
        MFMain.registerCommand(event);
    }

    @Listener
    public void registerData(RegisterDataEvent event) {
        event.register(getData(FishItemStackConverter.fishLengthKey(), (dataView, length) -> dataView.set(FishItemStackConverter.fishLengthQuery(), length), dataView -> dataView.getDouble(FishItemStackConverter.fishLengthQuery())));
        event.register(getData(FishItemStackConverter.fishTypeKey(), (dataView, type) -> dataView.set(FishItemStackConverter.fishTypeQuery(), type.name()), dataView -> dataView.getString(FishItemStackConverter.fishTypeQuery()).flatMap(type -> getFishTypeTable().getTypes().stream().filter(t -> t.name().equals(type)).findFirst())));
    }
}
