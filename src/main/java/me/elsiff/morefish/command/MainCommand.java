package me.elsiff.morefish.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.shop.FishShop;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

@CommandAlias("morefish|mf|fish")
public final class MainCommand extends BaseCommand {

    private final FishingCompetition competition;
    private final FishingCompetitionHost competitionHost;
    private final FishShop fishShop;
    private final MoreFish moreFish;
    private final PluginDescriptionFile pluginInfo;

    public MainCommand(@Nonnull MoreFish moreFish, @Nonnull FishingCompetitionHost competitionHost, @Nonnull FishShop fishShop) {
        super();
        this.moreFish = moreFish;
        this.competitionHost = competitionHost;
        this.fishShop = fishShop;
        this.pluginInfo = this.moreFish.getDescription();
        this.competition = this.competitionHost.getCompetition();
    }

    @Subcommand("begin|start")
    @CommandPermission("morefish.admin")
    public final void begin(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (!this.competition.isEnabled()) {
            if (args.length == 1) {
                try {
                    long runningTime = Long.parseLong(args[0]);
                    if (runningTime < 0L) {
                        sender.sendMessage(Lang.INSTANCE.text("not-positive"));
                    }
                    else {
                        this.competitionHost.openCompetitionFor(runningTime * (long) 20);
                        if (!Config.INSTANCE.getStandard().getBoolean("messages.broadcast-start", false)) {
                            sender.sendMessage(Lang.INSTANCE.format("contest-start-timer").replace(ImmutableMap.of("%time%", Lang.INSTANCE.time(runningTime))).output());
                        }
                    }
                }
                catch (NumberFormatException var6) {
                    String msg = Lang.INSTANCE.format("not-number").replace(ImmutableMap.of("%s", args[0])).output();
                    sender.sendMessage(msg);
                }
            }
            else {
                competitionHost.openCompetitionFor(Config.INSTANCE.getStandard().getInt("auto-running.timer"));
                if (!Config.INSTANCE.getStandard().getBoolean("messages.broadcast-start", false)) {
                    sender.sendMessage(Lang.INSTANCE.text("contest-start"));
                }
            }
        }
        else {
            sender.sendMessage(Lang.INSTANCE.text("already-ongoing"));
        }

    }

    @Subcommand("clear")
    @CommandPermission("morefish.admin")
    public final void clear(@Nonnull CommandSender sender) {
        this.competition.clearRecords();
        sender.sendMessage(Lang.INSTANCE.text("clear-records"));
    }

    @Subcommand("end")
    @CommandPermission("morefish.admin")
    public final void end(@Nonnull CommandSender sender) {
        if (!this.competition.isDisabled()) {
            competitionHost.closeCompetition();
            if (!Config.INSTANCE.getStandard().getBoolean("messages.broadcast-stop", false)) {
                sender.sendMessage(Lang.INSTANCE.text("contest-stop"));
            }
        }
        else {
            sender.sendMessage(Lang.INSTANCE.text("already-stopped"));
        }

    }

    @Default
    @Subcommand("help")
    @CommandPermission("morefish.help")
    public final void help(@Nonnull CommandSender sender) {
        String pluginName = pluginInfo.getName();
        String prefix = ChatColor.AQUA + "[" + pluginName + "]" + ChatColor.RESET + " ";
        sender.sendMessage(prefix + ChatColor.DARK_AQUA + "> ===== " + ChatColor.AQUA + ChatColor.BOLD + pluginName + ' ' + ChatColor.AQUA + 'v' + this.pluginInfo.getVersion() + ChatColor.DARK_AQUA + " ===== <");
        String label = this.getExecCommandLabel();
        sender.sendMessage(prefix + '/' + label + " help");
        sender.sendMessage(prefix + '/' + label + " begin [runningTime(sec)]");
        sender.sendMessage(prefix + '/' + label + " suspend");
        sender.sendMessage(prefix + '/' + label + " end");
        sender.sendMessage(prefix + '/' + label + " rewards");
        sender.sendMessage(prefix + '/' + label + " clear");
        sender.sendMessage(prefix + '/' + label + " reload");
        sender.sendMessage(prefix + '/' + label + " top");
        sender.sendMessage(prefix + '/' + label + " shop [player]");
    }

    @Subcommand("reload")
    @CommandPermission("morefish.admin")
    public final void reload(@Nonnull CommandSender sender) {
        try {
            this.moreFish.applyConfig();
            sender.sendMessage(Lang.INSTANCE.text("reload-config"));
        }
        catch (Exception var3) {
            var3.printStackTrace();
            sender.sendMessage(Lang.INSTANCE.text("failed-to-reload"));
        }

    }

    @Subcommand("shop")
    public final void shop(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player guiUser;
        if (args.length == 1) {
            if (!sender.hasPermission("morefish.admin")) {
                sender.sendMessage(Lang.INSTANCE.text("no-permission"));
                return;
            }

            Player target = sender.getServer().getPlayerExact(args[0]);
            if (target == null) {
                String msg = Lang.INSTANCE.format("player-not-found").replace(ImmutableMap.of("%s", args[0])).output();
                sender.sendMessage(msg);
                return;
            }

            guiUser = target;
        }
        else {
            if (!sender.hasPermission("morefish.shop")) {
                sender.sendMessage(Lang.INSTANCE.text("no-permission"));
                return;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(Lang.INSTANCE.text("in-game-command"));
                return;
            }

            guiUser = (Player) sender;
        }

        if (!this.fishShop.getEnabled()) {
            sender.sendMessage(Lang.INSTANCE.text("shop-disabled"));
        }
        else {
            this.fishShop.openGuiTo(guiUser);
            if (!guiUser.getUniqueId().equals(((Player) sender).getUniqueId())) {
                String msg = Lang.INSTANCE.format("forced-player-to-shop").replace(ImmutableMap.of("%s", guiUser.getName())).output();
                sender.sendMessage(msg);
            }
        }

    }

    @Subcommand("suspend")
    @CommandPermission("morefish.admin")
    public final void suspend(@Nonnull CommandSender sender) {
        if (!this.competition.isDisabled()) {
            this.competitionHost.closeCompetition(true);
            if (!Config.INSTANCE.getStandard().getBoolean("messages.broadcast-stop", false)) {
                sender.sendMessage(Lang.INSTANCE.text("contest-stop"));
            }
        }
        else {
            sender.sendMessage(Lang.INSTANCE.text("already-stopped"));
        }

    }

    @Subcommand("top|ranking")
    @CommandPermission("morefish.top")
    public final void top(@Nonnull CommandSender sender) {
        this.competitionHost.informAboutRanking(sender);
    }
}
