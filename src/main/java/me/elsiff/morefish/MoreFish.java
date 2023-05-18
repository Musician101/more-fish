package me.elsiff.morefish;

import java.time.LocalTime;
import java.util.List;
import javax.annotation.Nonnull;
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
import me.elsiff.morefish.hooker.ProtocolLibHooker;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.shop.FishShop;
import me.elsiff.morefish.shop.FishShopSignListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static io.musician101.bukkitier.Bukkitier.registerCommand;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static net.kyori.adventure.text.Component.text;

public final class MoreFish extends JavaPlugin {

    @Nonnull
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    @Nonnull
    private final FishingCompetition competition = new FishingCompetition();
    @Nonnull
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    @Nonnull
    private final FishBags fishBags = new FishBags();
    @Nonnull
    private final FishShop fishShop = new FishShop();
    @Nonnull
    private final FishTypeTable fishTypeTable = new FishTypeTable();
    @Nonnull
    private final McmmoHooker mcmmo = new McmmoHooker();
    @Nonnull
    private final ProtocolLibHooker protocolLib = new ProtocolLibHooker();
    @Nonnull
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

    @Nonnull
    public FishingCompetition getCompetition() {
        return competition;
    }

    @Nonnull
    public FishingCompetitionHost getCompetitionHost() {
        return competitionHost;
    }

    @Nonnull
    public FishBags getFishBags() {
        return fishBags;
    }

    @Nonnull
    public FishShop getFishShop() {
        return fishShop;
    }

    @Nonnull
    public FishTypeTable getFishTypeTable() {
        return fishTypeTable;
    }

    @Nonnull
    public McmmoHooker getMcmmo() {
        return mcmmo;
    }

    @Nonnull
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
        protocolLib.hookIfEnabled(this);
        vault.hookIfEnabled(this);
        mcmmo.hookIfEnabled(this);
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
