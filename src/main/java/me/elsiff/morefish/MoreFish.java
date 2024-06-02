package me.elsiff.morefish;

import io.musician101.bukkitier.Bukkitier;
import me.elsiff.morefish.command.MFMain;
import me.elsiff.morefish.fishing.FishBags;
import me.elsiff.morefish.fishing.FishTypeTable;
import me.elsiff.morefish.fishing.FishingListener;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionAutoRunner;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.fishing.fishrecords.FishingLogs;
import me.elsiff.morefish.hooker.McmmoHooker;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.text.Lang;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class MoreFish extends JavaPlugin {

    @NotNull
    private final FishingLogs fishingLogs = new FishingLogs();
    @NotNull
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    @NotNull
    private final FishingCompetition competition = new FishingCompetition();
    @NotNull
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    @NotNull
    private final FishBags fishBags = new FishBags();
    @NotNull
    private final FishTypeTable fishTypeTable = new FishTypeTable();
    @NotNull
    private final McmmoHooker mcmmo = new McmmoHooker();
    @NotNull
    private final MusiBoardHooker musiBoard = new MusiBoardHooker();
    @NotNull
    private final VaultHooker vault = new VaultHooker();
    private boolean loadingConfig = false;

    public static MoreFish getPlugin() {
        return getPlugin(MoreFish.class);
    }

    public void applyConfig() {
        loadingConfig = true;
        Bukkit.getAsyncScheduler().runNow(this, task -> {
            getServer().getOnlinePlayers().forEach(player -> {
                Component title = player.getOpenInventory().title();
                if (title.equals(Lang.replace("<mf-lang:shop-gui-title>")) || title.equals(Lang.replace("<mf-lang:sales-filter-title>"))) {
                    player.getScheduler().run(this, t -> {
                        player.closeInventory();
                        player.sendMessage(Lang.replace("<mf-lang:gui-closed-config-update>"));
                    }, null);
                }
            });

            saveDefaultConfig();
            reloadConfig();
            fishTypeTable.load();
            getSLF4JLogger().info("Loaded {} rarities and {} fish types", fishTypeTable.getRarities().size(), fishTypeTable.getTypes().size());
            if (autoRunner.isEnabled()) {
                autoRunner.disable();
            }

            if (getConfig().getBoolean("auto-running.enable")) {
                autoRunner.enable();
            }

            Lang.reload();
            getSLF4JLogger().info("Loaded language configuration file.");
            loadingConfig = false;
        });
    }

    @NotNull
    public FishingLogs getFishingLogs() {
        return fishingLogs;
    }

    @NotNull
    public FishingCompetition getCompetition() {
        return competition;
    }

    @NotNull
    public FishingCompetitionHost getCompetitionHost() {
        return competitionHost;
    }

    @NotNull
    public FishingCompetitionAutoRunner getAutoRunner() {
        return autoRunner;
    }

    @NotNull
    public FishBags getFishBags() {
        return fishBags;
    }

    @NotNull
    public FishTypeTable getFishTypeTable() {
        return fishTypeTable;
    }

    @NotNull
    public McmmoHooker getMcmmo() {
        return mcmmo;
    }

    @NotNull
    public MusiBoardHooker getMusiBoard() {
        return musiBoard;
    }

    @NotNull
    public VaultHooker getVault() {
        return vault;
    }

    @Override
    public void onDisable() {
        if (competition.isEnabled()) {
            competitionHost.closeCompetition();
        }

        fishBags.save();
        fishingLogs.save();
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        getSLF4JLogger().info("Plugin has been disabled.");
    }

    @Override
    public void onEnable() {
        vault.hook();
        mcmmo.hook();
        musiBoard.hook();
        applyConfig();
        fishBags.load();
        fishingLogs.load();
        Server server = getServer();
        PluginManager pm = server.getPluginManager();
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(fishBags, this);
        Bukkitier.registerCommand(getPlugin(), new MFMain(), "mf");
        getSLF4JLogger().info("Plugin has been enabled.");
    }

    public boolean isConfigLoading() {
        return loadingConfig;
    }
}
