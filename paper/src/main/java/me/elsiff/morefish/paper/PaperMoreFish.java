package me.elsiff.morefish.paper;

import java.time.LocalTime;
import java.util.List;
import me.elsiff.morefish.common.MoreFish;
import me.elsiff.morefish.paper.command.MFMain;
import me.elsiff.morefish.paper.configuration.Config;
import me.elsiff.morefish.paper.fishing.FishingListener;
import me.elsiff.morefish.paper.fishing.PaperFishBags;
import me.elsiff.morefish.paper.fishing.PaperFishTypeTable;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetitionAutoRunner;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetitionHost;
import me.elsiff.morefish.paper.hooker.McmmoHooker;
import me.elsiff.morefish.paper.hooker.MusiBoardHooker;
import me.elsiff.morefish.paper.hooker.ProtocolLibHooker;
import me.elsiff.morefish.paper.hooker.VaultHooker;
import me.elsiff.morefish.paper.shop.FishShopSignListener;
import me.elsiff.morefish.paper.shop.PaperFishShop;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static io.musician101.bukkitier.Bukkitier.registerCommand;
import static me.elsiff.morefish.common.configuration.Lang.SHOP_GUI_TITLE;
import static me.elsiff.morefish.common.configuration.Lang.join;
import static me.elsiff.morefish.paper.configuration.PaperLang.PREFIX;
import static net.kyori.adventure.text.Component.text;

public final class PaperMoreFish extends JavaPlugin implements MoreFish<PaperFishBags, PaperFishingCompetition, PaperFishingCompetitionHost, PaperFishTypeTable> {

    @NotNull private final PaperFishingCompetitionAutoRunner autoRunner = new PaperFishingCompetitionAutoRunner();
    @NotNull private final PaperFishingCompetition competition = new PaperFishingCompetition();
    @NotNull private final PaperFishingCompetitionHost competitionHost = new PaperFishingCompetitionHost();
    @NotNull private final PaperFishBags fishBags = new PaperFishBags();
    @NotNull private final PaperFishShop fishShop = new PaperFishShop();
    @NotNull private final PaperFishTypeTable fishTypeTable = new PaperFishTypeTable();
    @NotNull private final McmmoHooker mcmmo = new McmmoHooker();
    @NotNull private final MusiBoardHooker musiBoard = new MusiBoardHooker();
    @NotNull private final ProtocolLibHooker protocolLib = new ProtocolLibHooker();
    @NotNull private final VaultHooker vault = new VaultHooker();

    public static PaperMoreFish getPlugin() {
        return getPlugin(PaperMoreFish.class);
    }

    public void applyConfig() {
        getServer().getOnlinePlayers().forEach(player -> {
            Component title = player.getOpenInventory().title();
            if (title.equals(SHOP_GUI_TITLE) || title.equals(text("Set Sale Filter(s)"))) {
                player.closeInventory();
                player.sendMessage(join(PREFIX, text(" The config is being updated. To prevent issues, the window has been closed.")));
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
    @Override
    public PaperFishingCompetition getCompetition() {
        return competition;
    }

    @NotNull
    @Override
    public PaperFishingCompetitionHost getCompetitionHost() {
        return competitionHost;
    }

    @NotNull
    @Override
    public PaperFishBags getFishBags() {
        return fishBags;
    }

    @NotNull
    @Override
    public PaperFishShop getFishShop() {
        return fishShop;
    }

    @NotNull
    @Override
    public PaperFishTypeTable getFishTypeTable() {
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
        fishBags.save();
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        getLogger().info("Plugin has been disabled.");
    }

    @Override
    public void onEnable() {
        protocolLib.hookIfEnabled();
        vault.hookIfEnabled();
        mcmmo.hookIfEnabled();
        musiBoard.hookIfEnabled();
        fishBags.load();
        applyConfig();
        Server server = getServer();
        PluginManager pm = server.getPluginManager();
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(new FishShopSignListener(), this);
        pm.registerEvents(fishBags, this);
        registerCommand(getPlugin(), new MFMain());
        getLogger().info("Plugin has been enabled.");
        if (getConfig().getBoolean("general.auto-start")) {
            competitionHost.openCompetition();
        }
    }
}
