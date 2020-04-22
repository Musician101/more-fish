package me.elsiff.morefish.command;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.shop.FishShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public final class MainCommand implements TabExecutor {

    private final FishingCompetition competition;
    private final FishingCompetitionHost competitionHost;
    private final FishShop fishShop;
    private final MoreFish moreFish = MoreFish.instance();

    public MainCommand() {
        this.competitionHost = moreFish.getCompetitionHost();
        this.fishShop = moreFish.getFishShop();
        this.competition = this.competitionHost.getCompetition();
    }

    private void begin(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender.hasPermission("morefish.admin")) {
            sender.sendMessage(Lang.INSTANCE.text("no-permission"));
            return;
        }

        if (!this.competition.isEnabled()) {
            if (args.length == 1) {
                try {
                    long runningTime = Long.parseLong(args[0]);
                    if (runningTime < 0L) {
                        sender.sendMessage(Lang.INSTANCE.text("not-positive"));
                    }
                    else {
                        this.competitionHost.openCompetitionFor(runningTime * 20L);
                        if (!Config.INSTANCE.getStandard().getBoolean("messages.broadcast-start", false)) {
                            sender.sendMessage(Lang.INSTANCE.format("contest-start-timer").replace(ImmutableMap.of("%time%", Lang.INSTANCE.time(runningTime))).output());
                        }
                    }
                }
                catch (NumberFormatException e) {
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

    private void clear(@Nonnull CommandSender sender) {
        if (sender.hasPermission("morefish.admin")) {
            sender.sendMessage(Lang.INSTANCE.text("no-permission"));
            return;
        }

        this.competition.clearRecords();
        sender.sendMessage(Lang.INSTANCE.text("clear-records"));
    }

    private void end(@Nonnull CommandSender sender) {
        if (sender.hasPermission("morefish.admin")) {
            sender.sendMessage(Lang.INSTANCE.text("no-permission"));
            return;
        }

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

    private void help(@Nonnull CommandSender sender) {
        if (sender.hasPermission("morefish.help")) {
            sender.sendMessage(Lang.INSTANCE.text("no-permission"));
            return;
        }

        PluginDescriptionFile pluginInfo = moreFish.getDescription();
        String pluginName = pluginInfo.getName();
        String prefix = ChatColor.AQUA + "[" + pluginName + "]" + ChatColor.RESET + " ";
        sender.sendMessage(prefix + ChatColor.DARK_AQUA + "> ===== " + ChatColor.AQUA + ChatColor.BOLD + pluginName + ' ' + ChatColor.AQUA + 'v' + pluginInfo.getVersion() + ChatColor.DARK_AQUA + " ===== <");
        String label = prefix + "/mf";
        sender.sendMessage(label + " help");
        sender.sendMessage(label + " begin [runningTime(sec)]");
        sender.sendMessage(label + " suspend");
        sender.sendMessage(label + " end");
        sender.sendMessage(label + " rewards");
        sender.sendMessage(label + " clear");
        sender.sendMessage(label + " reload");
        sender.sendMessage(label + " top");
        sender.sendMessage(label + " shop [player]");
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "begin":
                case "start":
                    begin(sender, shiftArgs(args));
                    break;
                case "clear":
                    clear(sender);
                    break;
                case "end":
                    end(sender);
                    break;
                case "reload":
                    reload(sender);
                    break;
                case "shop":
                    shop(sender, shiftArgs(args));
                    break;
                case "suspend":
                    suspend(sender);
                    break;
                case "top":
                case "ranking":
                    top(sender);
                    break;
            }
        }

        help(sender);
        return true;
    }

    @Nonnull
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        if (args.length == 1) {

            return Stream.of("begin", "start", "clear", "end", "reload", "shop", "suspend", "top", "ranking").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        if (args.length > 1 && args[0].equalsIgnoreCase("shop") && sender.hasPermission("morefish.admin")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private void reload(@Nonnull CommandSender sender) {
        if (sender.hasPermission("morefish.admin")) {
            sender.sendMessage(Lang.INSTANCE.text("no-permission"));
            return;
        }

        try {
            this.moreFish.applyConfig();
            sender.sendMessage(Lang.INSTANCE.text("reload-config"));
        }
        catch (Exception var3) {
            var3.printStackTrace();
            sender.sendMessage(Lang.INSTANCE.text("failed-to-reload"));
        }
    }

    private String[] shiftArgs(@Nonnull String... args) {
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        return newArgs;
    }

    private void shop(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player guiUser;
        if (args.length == 1) {
            if (!sender.hasPermission("morefish.admin")) {
                sender.sendMessage(Lang.INSTANCE.text("no-permission"));
                return;
            }

            Player target = sender.getServer().getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(Lang.INSTANCE.format("player-not-found").replace(ImmutableMap.of("%s", args[0])).output());
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

    private void suspend(@Nonnull CommandSender sender) {
        if (sender.hasPermission("morefish.admin")) {
            sender.sendMessage(Lang.INSTANCE.text("no-permission"));
            return;
        }

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

    private void top(@Nonnull CommandSender sender) {
        if (sender.hasPermission("morefish.top")) {
            sender.sendMessage(Lang.INSTANCE.text("no-permission"));
            return;
        }

        this.competitionHost.informAboutRanking(sender);
    }
}
