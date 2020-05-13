package io.musician101.morefish.spigot;

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
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
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
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotMoreFish extends JavaPlugin {

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
}
