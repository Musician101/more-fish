package io.musician101.morefish.spigot;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import io.musician101.morefish.common.ConfigurateLoader;
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
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.command.MoreFishCommand;
import io.musician101.morefish.spigot.config.SpigotConfig;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.FishingListener;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchBroadcaster;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCompetitionRecordAdder;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotNewFirstBroadcaster;
import io.musician101.morefish.spigot.fishing.competition.FishingCompetitionAutoRunner;
import io.musician101.morefish.spigot.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.spigot.fishing.competition.SpigotPrize;
import io.musician101.morefish.spigot.fishing.condition.SpigotBiomeCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotCompetitionCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotEnchantmentCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotLocationYCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotMCMMOSkillCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotPotionEffectCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotRainingCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotThunderingCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotTimeCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotTimeCondition.TimeState;
import io.musician101.morefish.spigot.fishing.condition.SpigotWorldGuardRegionCondition;
import io.musician101.morefish.spigot.fishing.condition.SpigotXPLevelCondition;
import io.musician101.morefish.spigot.hooker.SpigotCitizensHooker;
import io.musician101.morefish.spigot.hooker.SpigotMCMMOHooker;
import io.musician101.morefish.spigot.hooker.SpigotPlaceholderApiHooker;
import io.musician101.morefish.spigot.hooker.SpigotProtocolLibHooker;
import io.musician101.morefish.spigot.hooker.SpigotVaultHooker;
import io.musician101.morefish.spigot.hooker.SpigotWorldGuardHooker;
import io.musician101.morefish.spigot.item.FishItemStackConverter;
import io.musician101.morefish.spigot.shop.FishShopSignListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public final class SpigotMoreFish extends JavaPlugin {

    @Nonnull
    private final FishConditionManager<SpigotFishCondition> fishConditionManager = new FishConditionManager<>();
    @Nonnull
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    @Nonnull
    private final SpigotCitizensHooker citizensHooker = new SpigotCitizensHooker();
    @Nonnull
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    @Nonnull
    private final FishItemStackConverter converter = new FishItemStackConverter();
    @SuppressWarnings("ConstantConditions")
    @Nonnull
    private final FishBags<ItemStack> fishBags = new FishBags<>(new File(getDataFolder(), "fish_bags"), ConfigurateLoader.YAML, ".yml", node -> ItemStack.deserialize(((Map<?, ?>) node.getValue()).entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Entry::getValue))));
    @Nonnull
    private final List<SpigotCatchHandler> globalCatchHandlers = Arrays.asList(new SpigotCatchBroadcaster(), new SpigotNewFirstBroadcaster(), new SpigotCompetitionRecordAdder());
    @Nonnull
    private final SpigotMCMMOHooker mcmmoHooker = new SpigotMCMMOHooker();
    @Nonnull
    private final SpigotPlaceholderApiHooker placeholderApiHooker = new SpigotPlaceholderApiHooker();
    @Nonnull
    private final SpigotProtocolLibHooker protocolLib = new SpigotProtocolLibHooker();
    @Nonnull
    private final SpigotVaultHooker vault = new SpigotVaultHooker();
    @Nonnull
    private final SpigotWorldGuardHooker worldGuardHooker = new SpigotWorldGuardHooker();
    @Nonnull
    private final Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> config = SpigotConfig.defaultConfig();
    @Nonnull
    private final FishingCompetition<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> competition = new FishingCompetition<>(new Records<>(new File(getDataFolder(), "records"), ConfigurateLoader.YAML, () -> config.getFishConfig().getTypes().stream()));

    public static SpigotMoreFish getInstance() {
        return getPlugin(SpigotMoreFish.class);
    }

    public final void applyConfig() {
        config.reload();
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
    public final FishingCompetitionAutoRunner getAutoRunner() {
        return autoRunner;
    }

    @Nonnull
    public final SpigotCitizensHooker getCitizensHooker() {
        return citizensHooker;
    }

    @Nonnull
    public final FishingCompetition<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> getCompetition() {
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
    public FishBags<ItemStack> getFishBags() {
        return fishBags;
    }

    @Nonnull
    public FishConditionManager<SpigotFishCondition> getFishConditionManager() {
        return fishConditionManager;
    }

    @Nonnull
    public final List<SpigotCatchHandler> getGlobalCatchHandlers() {
        return globalCatchHandlers;
    }

    @Nonnull
    public final SpigotMCMMOHooker getMCMMOHooker() {
        return mcmmoHooker;
    }

    @Nonnull
    public final SpigotPlaceholderApiHooker getPlaceholderApiHooker() {
        return placeholderApiHooker;
    }

    public Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> getPluginConfig() {
        return config;
    }

    @Nonnull
    public final SpigotProtocolLibHooker getProtocolLib() {
        return protocolLib;
    }

    @Nonnull
    public final SpigotVaultHooker getVault() {
        return vault;
    }

    @Nonnull
    public final SpigotWorldGuardHooker getWorldGuardHooker() {
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
        mcmmoHooker.hookIfEnabled();
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

    @SuppressWarnings("ConstantConditions")
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
        fishConditionManager.registerFishCondition("mcmmo-skill", args -> new SpigotMCMMOSkillCondition(PrimarySkillType.getSkill(args[0]), Integer.parseInt(args[1])));
        fishConditionManager.registerFishCondition("worldguard-region", args -> new SpigotWorldGuardRegionCondition(args[0]));
    }
}
