package me.elsiff.morefish;

import io.musician101.musicommand.paper.PaperMusiCommand;
import me.elsiff.morefish.bags.FishBags;
import me.elsiff.morefish.command.MFMain;
import me.elsiff.morefish.competition.FishingCompetition;
import me.elsiff.morefish.competition.FishingCompetitionAutoRunner;
import me.elsiff.morefish.competition.FishingCompetitionHost;
import me.elsiff.morefish.fish.FishTypeTable;
import me.elsiff.morefish.fish.FishingListener;
import me.elsiff.morefish.hooker.McmmoHooker;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.lang.Lang;
import me.elsiff.morefish.lang.TagResolverUtil;
import me.elsiff.morefish.records.FishingLogs;
import me.elsiff.morefish.shop.FishShopFilterDialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.util.List;

@NullMarked
public final class MoreFish extends JavaPlugin {

    private final FishingLogs fishingLogs = new FishingLogs();
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    private final FishingCompetition competition = new FishingCompetition();
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    private final FishBags fishBags = new FishBags();
    private final FishTypeTable fishTypeTable = new FishTypeTable();
    private final McmmoHooker mcmmo = new McmmoHooker();
    private final MusiBoardHooker musiBoard = new MusiBoardHooker();
    private final VaultHooker vault = new VaultHooker();
    private final Lang lang = new Lang();

    public static MoreFish getPlugin() {
        return getPlugin(MoreFish.class);
    }

    public static Lang lang() {
        return getPlugin().lang;
    }

    private void closeGUI() {
        getServer().getOnlinePlayers().forEach(player -> {
            if (FishShopFilterDialog.FILTERS.containsKey(player.getUniqueId())) {
                player.getScheduler().run(this, t -> player.closeDialog(), null);
            }

            Component title = player.getOpenInventory().title();
            if (title.equals(lang.getComponent("main", "shop", "gui-title"))) {
                player.getScheduler().run(this, t -> {
                    player.closeInventory();
                    player.sendMessage(lang.getComponent("gui", "closed-config-update"));
                }, null);
            }

        });
    }

    public void applyMainConfig() {
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        closeGUI();
        saveDefaultConfig();
        reloadConfig();
        if (getConfig().getBoolean("auto-running.enable")) {
            autoRunner.enable();
        }

        getComponentLogger().info(lang.getComponent(NodePath.path("main", "config", "config", "success")));
    }

    public void applyFishConfig() {
        closeGUI();
        NodePath path = NodePath.path("main", "config", "fish");
        try {
            fishTypeTable.load();
            TagResolver rarityCount = Formatter.number("rarity-count", fishTypeTable.getRarities().size());
            TagResolver typeCount = Formatter.number("type-count", fishTypeTable.getTypes().size());
            TagResolver resolver = TagResolver.resolver(rarityCount, typeCount);
            getComponentLogger().info(lang.getComponent(path.withAppendedChild("success"), resolver));
        }
        catch (IOException e) {
            getComponentLogger().info(lang.getComponent(path.withAppendedChild("error"), TagResolverUtil.error(e.getMessage())), e);
        }
    }

    public void applyLangConfig() {
        NodePath path = NodePath.path("main", "config", "lang");
        try {
            lang.load();
            getComponentLogger().info(lang.getComponent(path.withAppendedChild("success")));
        }
        catch (Exception e) {
            getComponentLogger().info(lang.getComponent(path.withAppendedChild("error"), TagResolverUtil.error(e.getMessage())), e);
        }
    }

    public void applyConfig() {
        applyLangConfig();
        applyMainConfig();
        applyFishConfig();
    }

    public FishingLogs getFishingLogs() {
        return fishingLogs;
    }

    public FishingCompetition getCompetition() {
        return competition;
    }

    public FishingCompetitionHost getCompetitionHost() {
        return competitionHost;
    }

    public FishingCompetitionAutoRunner getAutoRunner() {
        return autoRunner;
    }

    public FishBags getFishBags() {
        return fishBags;
    }

    public FishTypeTable getFishTypeTable() {
        return fishTypeTable;
    }

    public McmmoHooker getMcmmo() {
        return mcmmo;
    }

    public MusiBoardHooker getMusiBoard() {
        return musiBoard;
    }

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

        getComponentLogger().info(lang.getComponent("main", "plugin", "disabled"));
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
        //Bukkitier.registerCommand(getPlugin(), new MFMain(), "mf");
        PaperMusiCommand.newAdventureInstance(this).registerCommand(new MFMain(), List.of("mf", "fish"));
        getComponentLogger().info(lang.getComponent("main", "plugin", "enabled"));
    }
}
