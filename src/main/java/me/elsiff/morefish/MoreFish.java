package me.elsiff.morefish;

import me.elsiff.morefish.command.MFMain;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.FishBags;
import me.elsiff.morefish.fishing.FishTypeTable;
import me.elsiff.morefish.fishing.FishingListener;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionAutoRunner;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.hooker.McmmoHooker;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import me.elsiff.morefish.hooker.ProtocolLibHooker;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.shop.FishShop;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.List;

import static io.musician101.bukkitier.Bukkitier.registerCommand;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static net.kyori.adventure.text.Component.text;

public final class MoreFish extends JavaPlugin {

    @NotNull
    private final RecordHandler allTimeRecords = new RecordHandler();
    @NotNull
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    @NotNull
    private final FishingCompetition competition = new FishingCompetition();
    @NotNull
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    @NotNull
    private final FishBags fishBags = new FishBags();
    @NotNull
    private final FishShop fishShop = new FishShop();
    @NotNull
    private final FishTypeTable fishTypeTable = new FishTypeTable();
    @NotNull
    private final McmmoHooker mcmmo = new McmmoHooker();
    @NotNull
    private final MusiBoardHooker musiBoard = new MusiBoardHooker();
    @NotNull
    private final ProtocolLibHooker protocolLib = new ProtocolLibHooker();
    @NotNull
    private final VaultHooker vault = new VaultHooker();

    public static MoreFish getPlugin() {
        return getPlugin(MoreFish.class);
    }

    public void applyConfig() {
        getServer().getOnlinePlayers().forEach(player -> {
            Component title = player.getOpenInventory().title();
            if (title.equals(Lang.SHOP_GUI_TITLE) || title.equals(text("Set Sale Filter(s)"))) {
                player.closeInventory();
                player.sendMessage(Lang.join(PREFIX, text(" The config is being updated. To prevent issues, the window has been closed.")));
            }
        });

        saveDefaultConfig();
        reloadConfig();
        fishTypeTable.load();
        getLogger().info("Loaded " + fishTypeTable.getRarities().size() + " rarities and " + fishTypeTable.getTypes().size() + " fish types");
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        if (getConfig().getBoolean("auto-running.enable")) {
            List<LocalTime> scheduledTimes = Config.getScheduledTimes();
            autoRunner.setScheduledTimes(scheduledTimes);
            autoRunner.enable();
        }
    }

    @NotNull
    public RecordHandler getAllTimeRecords() {
        return allTimeRecords;
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
    public FishBags getFishBags() {
        return fishBags;
    }

    @NotNull
    public FishShop getFishShop() {
        return fishShop;
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
        competitionHost.closeCompetition();
        fishBags.save();
        allTimeRecords.save();
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        getLogger().info("Plugin has been disabled.");
    }

    @Override
    public void onEnable() {
        protocolLib.hookIfEnabled(this);
        vault.hookIfEnabled(this);
        mcmmo.hookIfEnabled(this);
        musiBoard.hookIfEnabled(this);
        fishBags.load();
        applyConfig();
        allTimeRecords.load();
        Server server = getServer();
        PluginManager pm = server.getPluginManager();
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(fishBags, this);
        registerCommand(getPlugin(), new MFMain());
        getLogger().info("Plugin has been enabled.");
    }
}
