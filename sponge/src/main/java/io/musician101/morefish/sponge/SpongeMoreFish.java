package io.musician101.morefish.sponge;

import com.google.inject.Inject;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.config.AutoRunningConfig;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.Records;
import io.musician101.morefish.common.fishing.catchhandler.CatchCommandExecutor;
import io.musician101.morefish.common.fishing.catchhandler.CatchFireworkSpawner;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.FishingCompetition.State;
import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import io.musician101.morefish.common.fishing.condition.FishConditionManager;
import io.musician101.morefish.sponge.announcement.SpongeRangedAnnouncement;
import io.musician101.morefish.sponge.announcement.SpongeServerAnnouncement;
import io.musician101.morefish.sponge.command.MoreFishCommand;
import io.musician101.morefish.sponge.config.ItemStackSerializer;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.FishingListener;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchBroadcaster;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchCommandExecutor;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchFireworkSpawner;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCompetitionRecordAdder;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeNewFirstBroadcaster;
import io.musician101.morefish.sponge.fishing.competition.FishingCompetitionAutoRunner;
import io.musician101.morefish.sponge.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.sponge.fishing.competition.SpongePrize;
import io.musician101.morefish.sponge.fishing.condition.SpongeBiomeCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeCompetitionCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeEnchantmentCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeLocationYCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongePotionEffectCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeRainingCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeThunderingCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeTimeCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeTimeCondition.TimeState;
import io.musician101.morefish.sponge.fishing.condition.SpongeXPLevelCondition;
import io.musician101.morefish.sponge.hooker.SpongeEconomyHooker;
import io.musician101.morefish.sponge.item.FishItemStackConverter;
import io.musician101.morefish.sponge.shop.FishShopSignListener;
import io.musician101.morefish.sponge.util.NumberUtils.DoubleRange;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.RegisterDataEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryHolder;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@SuppressWarnings({"ConstantConditions", "unused"})
@Plugin(Reference.ID)
public class SpongeMoreFish {

    @Nonnull
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    @Nonnull
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    @Nonnull
    private final FishItemStackConverter converter = new FishItemStackConverter();
    @Nonnull
    private final SpongeEconomyHooker economy = new SpongeEconomyHooker();
    @Nonnull
    private final FishBags<ItemStack> fishBags = new FishBags<>(new File("config/" + Reference.ID + "/fish_bags"), ConfigurateLoader.HOCON, ".conf", TypeSerializerCollection.defaults().childBuilder().register(ItemStack.class, new ItemStackSerializer()).build(), ItemStack.class);
    @Nonnull
    private final FishConditionManager fishConditionManager = new FishConditionManager();
    @Nonnull
    private final List<CatchHandler> globalCatchHandlers = Arrays.asList(new SpongeCatchBroadcaster(), new SpongeNewFirstBroadcaster(), new SpongeCompetitionRecordAdder());
    @Nonnull
    private final PluginContainer pluginContainer;
    @Nonnull
    private final Config<SpongeTextFormat, SpongeTextListFormat, Component> config = new Config<>(new File("config/" + Reference.ID + "/config.conf"),
            ConfigurateLoader.HOCON,
            TypeSerializerCollection.defaults().childBuilder().register(Prize.class, new Prize.Serializer(SpongePrize::new)).register(MessagesConfig.class, new MessagesConfig.Serializer(new SpongeServerAnnouncement())).register(PlayerAnnouncement.class, new PlayerAnnouncement.Serializer(new SpongeServerAnnouncement(), SpongeRangedAnnouncement::new)).register(CatchCommandExecutor.class, new CatchCommandExecutor.Serializer(SpongeCatchCommandExecutor::new)).register(CatchFireworkSpawner.class, new CatchFireworkSpawner.Serializer(SpongeCatchFireworkSpawner::new)).register(FishCondition.class, new FishCondition.Serializer(getInstance().fishConditionManager)).build(),
            () -> saveDefaultFile("config.conf"),
            () -> {
                saveDefaultFile("locale/fish_" + getConfig().getLocale() + ".conf");
                saveDefaultFile("locale/lang_" + getConfig().getLocale() + ".conf");
            },
            (current, latest) -> {
                if (current < latest) {
                    TextComponent msg = (TextComponent) getConfig().getLangConfig().format("old-file").replace(Collections.singletonMap("%s", "config.conf")).output();
                    getLogger().warn(msg.content());
                }
            },
            node -> new SpongeTextFormat(node.getString()),
            nodes -> new SpongeTextListFormat(nodes.stream().map(ConfigurationNode::getString).collect(Collectors.toList())),
            seconds -> {
                LangConfig<SpongeTextFormat, SpongeTextListFormat, Component> langConfig = getConfig().getLangConfig();
                StringBuilder sb = new StringBuilder();
                Duration duration = Duration.ofSeconds(seconds);
                if (duration.toMinutes() > 0L) {
                    sb.append(duration.toMinutes()).append(langConfig.text("time-format-minutes")).append(" ");
                }

                sb.append(duration.getSeconds() % 60).append(langConfig.text("time-format-seconds"));
                return Component.text(sb.toString());
            },
            node -> LegacyComponentSerializer.legacyAmpersand().deserialize(node.getString()));
    @Nonnull
    private final FishingCompetition competition = new FishingCompetition(new Records(new File("config/" + Reference.ID + "/records"), ConfigurateLoader.YAML, () -> SpongeMoreFish.getInstance().getConfig().getFishConfig().getTypes().stream()));
    @Inject
    public SpongeMoreFish(@Nonnull PluginContainer pluginContainer) {
        this.pluginContainer = pluginContainer;
    }

    private void saveDefaultFile(String file) {
        AssetManager assetManager = Sponge.assetManager();
        File dir = new File("config", Reference.ID);
        Sponge.assetManager().asset(getPluginContainer(), file).ifPresent(asset -> {
            if (!new File(dir, file).exists()) {
                try {
                    asset.copyToDirectory(dir.toPath());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static SpongeMoreFish getInstance() {
        return Sponge.pluginManager().plugin(Reference.ID).map(PluginContainer::instance).filter(SpongeMoreFish.class::isInstance).map(SpongeMoreFish.class::cast).orElseThrow(() -> new IllegalStateException("MoreFish is not enabled!"));
    }

    public final void applyConfig() {
        try {
            config.reload();
        }
        catch (ConfigurateException e) {
            e.printStackTrace();
            return;
        }

        Sponge.server().onlinePlayers().forEach(player -> player.openInventory().map(container -> container.get(org.spongepowered.api.data.Keys.DISPLAY_NAME).filter(component -> component.equals(config.getLangConfig().text("shop-gui-title")) || component.equals(config.getLangConfig().text("Set Sale Filter(s)")))).filter(Optional::isPresent).ifPresent(container -> {
            player.closeInventory();
            player.sendMessage(Component.join(Component.text(), Component.text("[MoreFish]", NamedTextColor.AQUA), Component.text(" The config is being updated. To prevent issues, the window has been closed.", NamedTextColor.WHITE)));
        }));
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        AutoRunningConfig autoRunningConfig = config.getAutoRunningConfig();
        if (autoRunningConfig.isEnabled()) {
            autoRunner.setScheduledTimes(autoRunningConfig.getStartTimes());
            autoRunner.enable();
        }
    }

    @Nonnull
    public final FishingCompetitionAutoRunner getAutoRunner() {
        return autoRunner;
    }

    @Nonnull
    public final FishingCompetition getCompetition() {
        return competition;
    }

    @Nonnull
    public final FishingCompetitionHost getCompetitionHost() {
        return competitionHost;
    }

    @Nonnull
    public Config<SpongeTextFormat, SpongeTextListFormat, Component> getConfig() {
        return config;
    }

    @Nonnull
    public final FishItemStackConverter getConverter() {
        return converter;
    }

    @Nonnull
    public final SpongeEconomyHooker getEconomy() {
        return economy;
    }

    @Nonnull
    public FishBags<ItemStack> getFishBags() {
        return fishBags;
    }

    @Nonnull
    public FishConditionManager getFishConditionManager() {
        return fishConditionManager;
    }

    @Nonnull
    public final List<CatchHandler> getGlobalCatchHandlers() {
        return globalCatchHandlers;
    }

    @Nonnull
    public Logger getLogger() {
        return pluginContainer.logger();
    }

    @Nonnull
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Listener
    public void onServerStart(StartingEngineEvent<Server> event) {
        economy.hookIfEnabled();
        fishBags.load();
        registerConditions();
        applyConfig();
        EventManager em = Sponge.eventManager();
        em.registerListeners(pluginContainer, new FishingListener());
        em.registerListeners(pluginContainer, new FishShopSignListener());
        getLogger().info("Plugin has been enabled.");
        if (config.autoStart()) {
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
        MoreFishCommand.init(event);
    }

    private void registerConditions() {
        RegistryHolder registryHolder = Sponge.game().registries();
        fishConditionManager.registerFishCondition("raining", args -> new SpongeRainingCondition(Boolean.parseBoolean(args[0])));
        fishConditionManager.registerFishCondition("thundering", args -> new SpongeThunderingCondition(Boolean.parseBoolean(args[0])));
        fishConditionManager.registerFishCondition("time", args -> new SpongeTimeCondition(TimeState.valueOf(args[0].toUpperCase())));
        fishConditionManager.registerFishCondition("biome", args -> new SpongeBiomeCondition(Stream.of(args).map(s -> registryHolder.registry(RegistryTypes.BIOME).findValue(ResourceKey.resolve(s)).orElseThrow(() -> new IllegalArgumentException(s + " is not a valid biome."))).collect(Collectors.toList())));
        fishConditionManager.registerFishCondition("enchantment", args -> new SpongeEnchantmentCondition(registryHolder.registry(RegistryTypes.ENCHANTMENT_TYPE).findValue(ResourceKey.resolve(args[0])).orElseThrow(() -> new IllegalArgumentException(args[0] + " is not a valid enchantment ID.")), Integer.parseInt(args[1])));
        fishConditionManager.registerFishCondition("level", args -> new SpongeXPLevelCondition(Integer.parseInt(args[0])));
        fishConditionManager.registerFishCondition("contest", args -> new SpongeCompetitionCondition(State.valueOf(args[0].toUpperCase())));
        fishConditionManager.registerFishCondition("potion-effect", args -> new SpongePotionEffectCondition(registryHolder.registry(RegistryTypes.POTION_EFFECT_TYPE).findValue(ResourceKey.resolve(args[0])).orElseThrow(() -> new IllegalArgumentException(args[0] + " is not a valid potion effect type ID.")), Integer.parseInt(args[1])));
        fishConditionManager.registerFishCondition("location-y", args -> new SpongeLocationYCondition(new DoubleRange(Double.parseDouble(args[0]), Double.parseDouble(args[1]))));
    }

    @Listener
    public void registerData(RegisterDataEvent event) {
        event.register(DataRegistration.of(FishItemStackConverter.FISH_LENGTH, ItemStack.class));
        event.register(DataRegistration.of(FishItemStackConverter.FISH_TYPE, ItemStack.class));
    }
}
