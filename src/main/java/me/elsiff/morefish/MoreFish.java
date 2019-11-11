package me.elsiff.morefish;

import co.aikar.commands.PaperCommandManager;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.egui.GuiOpener;
import me.elsiff.egui.GuiRegistry;
import me.elsiff.morefish.command.MainCommand;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.loader.CustomLoader;
import me.elsiff.morefish.dao.DaoFactory;
import me.elsiff.morefish.fishing.FishingListener;
import me.elsiff.morefish.fishing.MutableFishTypeTable;
import me.elsiff.morefish.fishing.catchhandler.CatchBroadcaster;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.catchhandler.CompetitionRecordAdder;
import me.elsiff.morefish.fishing.catchhandler.NewFirstBroadcaster;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionAutoRunner;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.hooker.CitizensHooker;
import me.elsiff.morefish.hooker.McmmoHooker;
import me.elsiff.morefish.hooker.PlaceholderApiHooker;
import me.elsiff.morefish.hooker.ProtocolLibHooker;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.hooker.WorldGuardHooker;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.shop.FishShop;
import me.elsiff.morefish.shop.FishShopSignListener;
import me.elsiff.morefish.update.UpdateChecker;
import me.elsiff.morefish.update.UpdateNotifierListener;
import me.elsiff.morefish.util.OneTickScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MoreFish extends JavaPlugin {

    @Nonnull
    private final FishingCompetitionAutoRunner autoRunner;
    @Nonnull
    private final CitizensHooker citizensHooker = new CitizensHooker();
    @Nonnull
    private final FishingCompetition competition;
    @Nonnull
    private final FishingCompetitionHost competitionHost;
    @Nonnull
    private final FishItemStackConverter converter;
    @Nonnull
    private final FishShop fishShop;
    @Nonnull
    private final MutableFishTypeTable fishTypeTable;
    @Nonnull
    private final List<CatchHandler> globalCatchHandlers;
    @Nonnull
    private final GuiOpener guiOpener;
    @Nonnull
    private final GuiRegistry guiRegistry = new GuiRegistry(this);
    @Nonnull
    private final McmmoHooker mcmmoHooker = new McmmoHooker();
    @Nonnull
    private final OneTickScheduler oneTickScheduler;
    @Nonnull
    private final PlaceholderApiHooker placeholderApiHooker = new PlaceholderApiHooker();
    @Nonnull
    private final ProtocolLibHooker protocolLib = new ProtocolLibHooker();
    @Nonnull
    private final UpdateChecker updateChecker;
    @Nonnull
    private final VaultHooker vault = new VaultHooker();
    @Nonnull
    private final WorldGuardHooker worldGuardHooker = new WorldGuardHooker();

    public MoreFish() {
        guiOpener = new GuiOpener(guiRegistry);
        oneTickScheduler = new OneTickScheduler(this);
        fishTypeTable = new MutableFishTypeTable();
        competition = new FishingCompetition();
        competitionHost = new FishingCompetitionHost(this, competition);
        autoRunner = new FishingCompetitionAutoRunner(this, competitionHost);
        converter = new FishItemStackConverter(this, fishTypeTable);
        fishShop = new FishShop(guiOpener, oneTickScheduler, converter, vault);
        globalCatchHandlers = Arrays.asList(new CatchBroadcaster(), new NewFirstBroadcaster(competition), new CompetitionRecordAdder(competition));
        updateChecker = new UpdateChecker(22926, getDescription().getVersion());
    }

    public static MoreFish instance() {
        return JavaPlugin.getPlugin(MoreFish.class);
    }

    public final void applyConfig() {
        Config.INSTANCE.load(this);
        Config.INSTANCE.getCustomItemStackLoader().setProtocolLib(protocolLib);
        Config.INSTANCE.getFishConditionSetLoader().init(mcmmoHooker, worldGuardHooker);
        fishTypeTable.clear();
        Config.INSTANCE.getFishTypeMapLoader().loadFrom(Config.INSTANCE.getFish(), CustomLoader.ROOT_PATH);
        fishTypeTable.clear();
        fishTypeTable.putAll(Config.INSTANCE.getFishTypeMapLoader().loadFrom(Config.INSTANCE.getFish(), CustomLoader.ROOT_PATH));
        getLogger().info("Loaded " + fishTypeTable.getRarities().size() + " rarities and " + fishTypeTable.getTypes().size() + " fish types");
        if (Config.INSTANCE.getStandard().getBoolean("auto-running.enable") && !autoRunner.isEnabled()) {
            List<LocalTime> scheduledTimes = Config.INSTANCE.getLocalTimeListLoader().loadFrom(Config.INSTANCE.getStandard(), "auto-running.start-time");
            autoRunner.setScheduledTimes(scheduledTimes);
            autoRunner.enable();
        }
    }

    @Nonnull
    public final FishingCompetitionAutoRunner getAutoRunner() {
        return autoRunner;
    }

    @Nonnull
    public final CitizensHooker getCitizensHooker() {
        return citizensHooker;
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
    public final FishItemStackConverter getConverter() {
        return converter;
    }

    @Nonnull
    public final FishShop getFishShop() {
        return fishShop;
    }

    @Nonnull
    public final MutableFishTypeTable getFishTypeTable() {
        return fishTypeTable;
    }

    @Nonnull
    public final List getGlobalCatchHandlers() {
        return globalCatchHandlers;
    }

    @Nonnull
    public final GuiOpener getGuiOpener() {
        return guiOpener;
    }

    @Nonnull
    public final GuiRegistry getGuiRegistry() {
        return guiRegistry;
    }

    @Nonnull
    public final McmmoHooker getMcmmoHooker() {
        return mcmmoHooker;
    }

    @Nonnull
    public final OneTickScheduler getOneTickScheduler() {
        return oneTickScheduler;
    }

    @Nonnull
    public final PlaceholderApiHooker getPlaceholderApiHooker() {
        return placeholderApiHooker;
    }

    @Nonnull
    public final ProtocolLibHooker getProtocolLib() {
        return protocolLib;
    }

    @Nonnull
    public final UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    @Nonnull
    public final VaultHooker getVault() {
        return vault;
    }

    @Nonnull
    public final WorldGuardHooker getWorldGuardHooker() {
        return worldGuardHooker;
    }

    private boolean isSnapshotVersion() {
        return getDescription().getVersion().contains("SNAPSHOT");
    }

    public void onDisable() {
        guiRegistry.clear(true);
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        if (citizensHooker.hasHooked()) {
            citizensHooker.dispose();
        }

        getLogger().info("Plugin has been disabled.");
    }

    public void onEnable() {
        DaoFactory.INSTANCE.init(this);
        protocolLib.hookIfEnabled(this);
        vault.hookIfEnabled(this);
        mcmmoHooker.hookIfEnabled(this);
        worldGuardHooker.hookIfEnabled(this);
        citizensHooker.hookIfEnabled(this);
        placeholderApiHooker.hookIfEnabled(this);
        applyConfig();
        Server server = getServer();
        PluginManager pm = server.getPluginManager();
        pm.registerEvents(new FishingListener(fishTypeTable, converter, competition, globalCatchHandlers), this);
        pm.registerEvents(new FishShopSignListener(fishShop), this);
        PaperCommandManager commands = new PaperCommandManager(this);
        MainCommand mainCommand = new MainCommand(this, competitionHost, fishShop);
        commands.registerCommand(mainCommand);
        if (!isSnapshotVersion()) {
            updateChecker.check();
            if (updateChecker.hasNewVersion()) {
                UpdateNotifierListener notifier = new UpdateNotifierListener(updateChecker.getNewVersion());
                server = getServer();
                server.getPluginManager().registerEvents(notifier, this);
            }
        }

        getLogger().info("Plugin has been enabled.");
        if (Config.INSTANCE.getStandard().getBoolean("general.auto-start")) {
            competitionHost.openCompetition();
        }

    }
}
