package io.musician101.morefish.sponge;

import com.google.inject.Inject;
import io.musician101.morefish.common.ConfigurateLoader;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.config.AutoRunningConfig;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.common.fishing.Records;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.FishingCompetition.State;
import io.musician101.morefish.common.fishing.condition.FishConditionManager;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.command.MoreFishCommand;
import io.musician101.morefish.sponge.config.SpongeConfig;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.data.ImmutableMoreFishData;
import io.musician101.morefish.sponge.data.MoreFishData;
import io.musician101.morefish.sponge.data.MoreFishDataBuilder;
import io.musician101.morefish.sponge.fishing.FishingListener;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchBroadcaster;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCompetitionRecordAdder;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeNewFirstBroadcaster;
import io.musician101.morefish.sponge.fishing.competition.FishingCompetitionAutoRunner;
import io.musician101.morefish.sponge.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.sponge.fishing.competition.SpongePrize;
import io.musician101.morefish.sponge.fishing.condition.SpongeBiomeCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeCompetitionCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeEnchantmentCondition;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
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
import io.musician101.musicianlibrary.java.minecraft.sponge.plugin.AbstractSpongePlugin;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.biome.BiomeType;

@Plugin(id = Reference.ID, name = Reference.NAME, version = Reference.VERSION, url = "https://github.com/Musician101", authors = {"elsiff", "Musician101"})
public class SpongeMoreFish extends AbstractSpongePlugin<Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize>> {

    @Nonnull
    private final FishConditionManager<SpongeFishCondition> fishConditionManager = new FishConditionManager<>();
    @Nonnull
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    @Nonnull
    private final FishingCompetition<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> competition = new FishingCompetition<>(new Records<>(new File("config/" + Reference.ID + "/records"), ConfigurateLoader.YAML, () -> SpongeMoreFish.getInstance().getConfig().getFishConfig().getTypes().stream()));
    @Nonnull
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    @Nonnull
    private final FishItemStackConverter converter = new FishItemStackConverter();
    @Nonnull
    private final SpongeEconomyHooker economy = new SpongeEconomyHooker();
    @Nonnull
    private final FishBags<ItemStack> fishBags = new FishBags<>(new File("config/" + Reference.ID + "/fish_bags"), ConfigurateLoader.HOCON, ".conf", node -> ItemStack.builder().fromContainer(DataTranslators.CONFIGURATION_NODE.translate(node)).build());
    @Nonnull
    private final List<SpongeCatchHandler> globalCatchHandlers = Arrays.asList(new SpongeCatchBroadcaster(), new SpongeNewFirstBroadcaster(), new SpongeCompetitionRecordAdder());
    @Inject
    private PluginContainer pluginContainer;
    @Nonnull
    private final Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> config = SpongeConfig.defaultConfig();

    public static SpongeMoreFish getInstance() {
        return Sponge.getPluginManager().getPlugin(Reference.ID).flatMap(PluginContainer::getInstance).filter(SpongeMoreFish.class::isInstance).map(SpongeMoreFish.class::cast).orElseThrow(() -> new IllegalStateException("MoreFish is not enabled!"));
    }

    public final void applyConfig() {
        config.reload();
        Sponge.getServer().getOnlinePlayers().forEach(player -> player.getOpenInventory().map(container -> container.<Container>query(QueryOperationTypes.INVENTORY_PROPERTY.of(new InventoryTitle(config.getLangConfig().text("shop-gui-title"))), QueryOperationTypes.INVENTORY_PROPERTY.of(new InventoryTitle(config.getLangConfig().text("Set Sale Filter(s)"))))).filter(container -> container.getViewers().contains(player)).ifPresent(container -> {
            player.closeInventory();
            player.sendMessage(Text.of(TextColors.AQUA, "[MoreFish]", TextColors.RESET, " The config is being updated. To prevent issues, the window has been closed."));
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
    public final FishingCompetition<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> getCompetition() {
        return competition;
    }

    @Nonnull
    public final FishingCompetitionHost getCompetitionHost() {
        return competitionHost;
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
    public FishConditionManager<SpongeFishCondition> getFishConditionManager() {
        return fishConditionManager;
    }

    @Nonnull
    public final List<SpongeCatchHandler> getGlobalCatchHandlers() {
        return globalCatchHandlers;
    }

    @Nonnull
    @Override
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        economy.hookIfEnabled();
        fishBags.load();
        registerConditions();
        applyConfig();
        EventManager em = Sponge.getEventManager();
        em.registerListeners(this, new FishingListener());
        em.registerListeners(this, new FishShopSignListener());
        MoreFishCommand.init();
        DataRegistration.builder().dataClass(MoreFishData.class).immutableClass(ImmutableMoreFishData.class).builder(new MoreFishDataBuilder()).id("more_fish_data").name("MoreFishData").build();
        getLogger().info("Plugin has been enabled.");
        if (config.autoStart()) {
            competitionHost.openCompetition();
        }
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        fishBags.save();
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }
        getLogger().info("Plugin has been disabled.");
    }

    private void registerConditions() {
        GameRegistry registry = Sponge.getRegistry();
        fishConditionManager.registerFishCondition("raining", args -> new SpongeRainingCondition(Boolean.parseBoolean(args[0])));
        fishConditionManager.registerFishCondition("thundering", args -> new SpongeThunderingCondition(Boolean.parseBoolean(args[0])));
        fishConditionManager.registerFishCondition("time", args -> new SpongeTimeCondition(TimeState.valueOf(args[0].toUpperCase())));
        fishConditionManager.registerFishCondition("biome", args -> new SpongeBiomeCondition(Stream.of(args).map(s -> registry.getType(BiomeType.class, s).orElseThrow(() -> new IllegalArgumentException(s + " is not a valid biome ID."))).collect(Collectors.toSet())));
        fishConditionManager.registerFishCondition("enchantment", args -> new SpongeEnchantmentCondition(registry.getType(EnchantmentType.class, args[0]).orElseThrow(() -> new IllegalArgumentException(args[0] + " is not a valid enchantment ID.")), Integer.parseInt(args[1])));
        fishConditionManager.registerFishCondition("level", args -> new SpongeXPLevelCondition(Integer.parseInt(args[0])));
        fishConditionManager.registerFishCondition("contest", args -> new SpongeCompetitionCondition(State.valueOf(args[0].toUpperCase())));
        fishConditionManager.registerFishCondition("potion-effect", args -> new SpongePotionEffectCondition(registry.getType(PotionEffectType.class, args[0]).orElseThrow(() -> new IllegalArgumentException(args[0] + " is not a valid potion effect type ID.")), Integer.parseInt(args[1])));
        fishConditionManager.registerFishCondition("location-y", args -> new SpongeLocationYCondition(new DoubleRange(Double.parseDouble(args[0]), Double.parseDouble(args[1]))));
    }
}
