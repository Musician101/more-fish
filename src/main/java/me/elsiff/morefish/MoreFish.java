package me.elsiff.morefish;

import io.musician101.musicommand.paper.PaperMusiCommand;
import me.elsiff.morefish.bags.FishBags;
import me.elsiff.morefish.command.MFMain;
import me.elsiff.morefish.competition.FishingCompetition;
import me.elsiff.morefish.competition.FishingCompetitionAutoRunner;
import me.elsiff.morefish.competition.FishingCompetitionHost;
import me.elsiff.morefish.fish.FishingListener;
import me.elsiff.morefish.fish.registry.FishRarities;
import me.elsiff.morefish.fish.registry.FishTypes;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.lang.ArgumentUtil;
import me.elsiff.morefish.lang.Lang;
import me.elsiff.morefish.records.FishingLogs;
import me.elsiff.morefish.shop.FishShopFilterDialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.util.List;

@NullMarked
public final class MoreFish extends JavaPlugin {

    private final FishingLogs fishingLogs = new FishingLogs();
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    private final FishingCompetition competition = new FishingCompetition();
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    private final FishBags fishBags = new FishBags();
    private final FishRarities rarities = new FishRarities();
    private final FishTypes types = new FishTypes();
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
                player.getScheduler().run(this, t -> {
                    player.closeDialog();
                    player.sendMessage(Component.translatable("morefish.gui.closed-config-update"));
                }, null);
            }

            Component title = player.getOpenInventory().title();
            if (title.equals(Component.translatable("morefish.main.shop.gui-title"))) {
                player.getScheduler().run(this, t -> {
                    player.closeInventory();
                    player.sendMessage(Component.translatable("morefish.gui.closed-config-update"));
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


        getComponentLogger().info(Component.translatable("morefish.main.config.config.success"));
    }

    public void applyFishConfig() {
        closeGUI();
        try {
            rarities.load();
            types.load();
            ComponentLike rarityCount = Argument.numeric("rarity-count", rarities.values().size());
            ComponentLike typeCount = Argument.numeric("type-count", types.values().size());
            Component message = Component.translatable("morefish.main.config.fish.success", rarityCount, typeCount);
            getComponentLogger().info(message);
        }
        catch (IOException e) {
            Component message = Component.translatable("morefish.main.config.fish.success", ArgumentUtil.error(e.getMessage()));
            getComponentLogger().info(message, e);
        }
    }

    public void applyLangConfig() {
        try {
            lang.load();
            getComponentLogger().info(Component.translatable("morefish.main.config.lang.success"));
        }
        catch (IOException e) {
            getSLF4JLogger().error("An error occurred while trying to load language files.", e);
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

    public FishRarities rarities() {
        return rarities;
    }

    public FishTypes types() {
        return types;
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

        getComponentLogger().info(Component.translatable("morefish.main.plugin.disabled"));
    }

    @Override
    public void onEnable() {
        vault.hook();
        musiBoard.hook();
        applyConfig();
        fishBags.load();
        fishingLogs.load();
        Server server = getServer();
        PluginManager pm = server.getPluginManager();
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(fishBags, this);
        PaperMusiCommand.newAdventureInstance(this).registerCommand(new MFMain(), List.of("mf", "fish"));
        getComponentLogger().info(Component.translatable("morefish.main.plugin.enabled"));
    }
}
