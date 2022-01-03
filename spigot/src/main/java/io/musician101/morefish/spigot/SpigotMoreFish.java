package io.musician101.morefish.spigot;

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
import io.musician101.morefish.spigot.announcement.SpigotRangedAnnouncement;
import io.musician101.morefish.spigot.announcement.SpigotServerAnnouncement;
import io.musician101.morefish.spigot.command.MoreFishCommand;
import io.musician101.morefish.spigot.config.ItemStackSerializer;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.FishingListener;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchBroadcaster;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchCommandExecutor;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchFireworkSpawner;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCompetitionRecordAdder;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotNewFirstBroadcaster;
import io.musician101.morefish.spigot.fishing.competition.FishingCompetitionAutoRunner;
import io.musician101.morefish.spigot.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.spigot.fishing.competition.SpigotPrize;
import io.musician101.morefish.spigot.fishing.condition.SpigotBiomeCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotCompetitionCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotEnchantmentCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotLocationYCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotPotionEffectCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotRainingCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotThunderingCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotTimeCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotTimeCondition.TimeState;
import io.musician101.morefish.spigot.fishing.condition.SpigotWorldGuardRegionCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotXPLevelCondition;
import io.musician101.morefish.spigot.hooker.SpigotCitizensHooker;
import io.musician101.morefish.spigot.hooker.SpigotPlaceholderApiHooker;
import io.musician101.morefish.spigot.hooker.SpigotProtocolLibHooker;
import io.musician101.morefish.spigot.hooker.SpigotVaultHooker;
import io.musician101.morefish.spigot.hooker.SpigotWorldGuardHooker;
import io.musician101.morefish.spigot.item.FishItemStackConverter;
import io.musician101.morefish.spigot.shop.FishShopSignListener;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@SuppressWarnings({"ConstantConditions", "unused"})
public final class SpigotMoreFish extends JavaPlugin {

    @Nonnull
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    @Nonnull
    private final SpigotCitizensHooker citizensHooker = new SpigotCitizensHooker();
    @Nonnull
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    @Nonnull
    private final FishItemStackConverter converter = new FishItemStackConverter();
    @Nonnull
    private final FishBags<ItemStack> fishBags = new FishBags<>(new File(getDataFolder(), "fish_bags"), ConfigurateLoader.YAML, ".yml", TypeSerializerCollection.defaults().childBuilder().register(ItemStack.class, new ItemStackSerializer()).build(), ItemStack.class);
    @Nonnull
    private final FishConditionManager fishConditionManager = new FishConditionManager();
    @Nonnull
    private final List<CatchHandler> globalCatchHandlers = Arrays.asList(new SpigotCatchBroadcaster(), new SpigotNewFirstBroadcaster(), new SpigotCompetitionRecordAdder());    @Nonnull
    private final Config<SpigotTextFormat, SpigotTextListFormat, String> config = new Config<>(new File(SpigotMoreFish.getInstance().getDataFolder(), "config.yml"), ConfigurateLoader.YAML, TypeSerializerCollection.defaults().childBuilder().register(Prize.class, new Prize.Serializer(SpigotPrize::new)).register(MessagesConfig.class, new MessagesConfig.Serializer(new SpigotServerAnnouncement())).register(PlayerAnnouncement.class, new PlayerAnnouncement.Serializer(new SpigotServerAnnouncement(), SpigotRangedAnnouncement::new)).register(CatchCommandExecutor.class, new CatchCommandExecutor.Serializer(SpigotCatchCommandExecutor::new)).register(CatchFireworkSpawner.class, new CatchFireworkSpawner.Serializer(SpigotCatchFireworkSpawner::new)).register(FishCondition.class, new FishCondition.Serializer(getInstance().fishConditionManager)).build(), this::saveDefaultConfig, () -> {
        saveDefaultFile("locale/fish_" + getPluginConfig().getLocale() + ".conf");
        saveDefaultFile("locale/lang_" + getPluginConfig().getLocale() + ".conf");
    }, (current, latest) -> {
        if (current < latest) {
            String msg = getPluginConfig().getLangConfig().format("old-file").replace(Collections.singletonMap("%s", "config.yml")).output();
            getLogger().warning(msg);
        }
    }, node -> new SpigotTextFormat(node.getString()), nodes -> new SpigotTextListFormat(nodes.stream().map(ConfigurationNode::getString).collect(Collectors.toList())), seconds -> {
        LangConfig<SpigotTextFormat, SpigotTextListFormat, String> langConfig = getPluginConfig().getLangConfig();
        StringBuilder sb = new StringBuilder();
        Duration duration = Duration.ofSeconds(seconds);
        if (duration.toMinutes() > 0L) {
            sb.append(duration.toMinutes()).append(langConfig.text("time-format-minutes")).append(" ");
        }

        sb.append(duration.getSeconds() % 60).append(langConfig.text("time-format-seconds"));
        return sb.toString();
    }, node -> ChatColor.translateAlternateColorCodes('&', node.getString()));
    @Nonnull
    private final SpigotPlaceholderApiHooker placeholderApiHooker = new SpigotPlaceholderApiHooker();    @Nonnull
    private final FishingCompetition competition = new FishingCompetition(new Records(new File(getDataFolder(), "records"), ConfigurateLoader.YAML, () -> config.getFishConfig().getTypes().stream()));
    @Nonnull
    private final SpigotProtocolLibHooker protocolLib = new SpigotProtocolLibHooker();
    @Nonnull
    private final SpigotVaultHooker vault = new SpigotVaultHooker();
    @Nonnull
    private final SpigotWorldGuardHooker worldGuardHooker = new SpigotWorldGuardHooker();

    public static SpigotMoreFish getInstance() {
        return getPlugin(SpigotMoreFish.class);
    }

    public void applyConfig() {
        try {
            config.reload();
        }
        catch (ConfigurateException e) {
            e.printStackTrace();
            return;
        }

        getServer().getOnlinePlayers().forEach(player -> {
            String title = player.getOpenInventory().getTitle();
            if (title.equals(config.getLangConfig().text("shop-gui-title")) || title.equals("Set Sale Filter(s)")) {
                player.closeInventory();
                player.sendMessage(ChatColor.AQUA + "[MoreFish]" + ChatColor.RESET + " The config is being updated. To prevent issues, the window has been closed.");
            }
        });

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
    public FishingCompetitionAutoRunner getAutoRunner() {
        return autoRunner;
    }

    @Nonnull
    public SpigotCitizensHooker getCitizensHooker() {
        return citizensHooker;
    }

    @Nonnull
    public FishingCompetition getCompetition() {
        return competition;
    }

    @Nonnull
    public FishingCompetitionHost getCompetitionHost() {
        return competitionHost;
    }

    @Nonnull
    public FishItemStackConverter getConverter() {
        return converter;
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
    public List<CatchHandler> getGlobalCatchHandlers() {
        return globalCatchHandlers;
    }

    @Nonnull
    public SpigotPlaceholderApiHooker getPlaceholderApiHooker() {
        return placeholderApiHooker;
    }

    public Config<SpigotTextFormat, SpigotTextListFormat, String> getPluginConfig() {
        return config;
    }

    @Nonnull
    public SpigotProtocolLibHooker getProtocolLib() {
        return protocolLib;
    }

    @Nonnull
    public SpigotVaultHooker getVault() {
        return vault;
    }

    @Nonnull
    public SpigotWorldGuardHooker getWorldGuardHooker() {
        return worldGuardHooker;
    }

    @Override
    public void onDisable() {
        fishBags.save();
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        if (citizensHooker.hasHooked()) {
            citizensHooker.dispose();
        }

        getLogger().info("Plugin has been disabled.");
    }

    @Override
    public void onEnable() {
        protocolLib.hookIfEnabled();
        vault.hookIfEnabled();
        worldGuardHooker.hookIfEnabled();
        citizensHooker.hookIfEnabled();
        placeholderApiHooker.hookIfEnabled();
        fishBags.load();
        registerConditions();
        applyConfig();
        Server server = getServer();
        PluginManager pm = server.getPluginManager();
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(new FishShopSignListener(), this);
        MoreFishCommand.init();
        getLogger().info("Plugin has been enabled.");
        if (config.autoStart()) {
            competitionHost.openCompetition();
        }
    }

    private void registerConditions() {
        fishConditionManager.registerFishCondition("raining", args -> new SpigotRainingCondition(Boolean.parseBoolean(args[0])));
        fishConditionManager.registerFishCondition("thundering", args -> new SpigotThunderingCondition(Boolean.parseBoolean(args[0])));
        fishConditionManager.registerFishCondition("time", args -> new SpigotTimeCondition(TimeState.valueOf(args[0].toUpperCase())));
        fishConditionManager.registerFishCondition("biome", args -> new SpigotBiomeCondition(Stream.of(args).map(String::toUpperCase).map(Biome::valueOf).collect(Collectors.toList())));
        fishConditionManager.registerFishCondition("enchantment", args -> new SpigotEnchantmentCondition(Enchantment.getByKey(NamespacedKey.minecraft(args[0])), Integer.parseInt(args[1])));
        fishConditionManager.registerFishCondition("level", args -> new SpigotXPLevelCondition(Integer.parseInt(args[0])));
        fishConditionManager.registerFishCondition("contest", args -> new SpigotCompetitionCondition(State.valueOf(args[0].toUpperCase())));
        fishConditionManager.registerFishCondition("potion-effect", args -> new SpigotPotionEffectCondition(PotionEffectType.getByName(args[0]), Integer.parseInt(args[1])));
        fishConditionManager.registerFishCondition("location-y", args -> new SpigotLocationYCondition(new DoubleRange(Double.parseDouble(args[0]), Double.parseDouble(args[1]))));
        fishConditionManager.registerFishCondition("worldguard-region", args -> new SpigotWorldGuardRegionCondition(args[0]));
    }

    private void saveDefaultFile(String file) {
        saveResource(file, false);
    }




}
