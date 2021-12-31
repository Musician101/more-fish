package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.shop.FishShop;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import static io.musician101.bukkitier.Bukkitier.argument;
import static io.musician101.bukkitier.Bukkitier.literal;
import static io.musician101.bukkitier.Bukkitier.registerCommand;

@SuppressWarnings("SameReturnValue")
public interface MFCommands {

    private static LiteralArgumentBuilder<CommandSender> begin(String name) {
        return literal(name).requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            CommandSender sender = context.getSource();
            if (getCompetition().isDisabled()) {
                getCompetitionHost().openCompetitionFor(getConfig().getInt("auto-running.timer") * 20L);
                if (!getConfig().getBoolean("messages.broadcast-start", false)) {
                    sender.sendMessage(Lang.CONTEST_START);
                }
            }
            else {
                sender.sendMessage(Lang.ALREADY_ONGOING);
            }

            return 1;
        }).then(argument("seconds", LongArgumentType.longArg(0)).executes(context -> {
            CommandSender sender = context.getSource();
            if (getCompetition().isDisabled()) {
                long runningTime = context.getArgument("seconds", Long.class);
                getCompetitionHost().openCompetitionFor(runningTime * 20L);
                if (!getConfig().getBoolean("messages.broadcast-start", false)) {
                    sender.sendMessage(Lang.replace(Lang.CONTEST_START_TIMER, Map.of("%time%", Lang.time(runningTime))));
                }
            }
            else {
                sender.sendMessage(Lang.ALREADY_ONGOING);
            }

            return 1;
        }));
    }

    private static LiteralArgumentBuilder<CommandSender> clear() {
        return literal("clear").requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            CommandSender sender = context.getSource();
            getCompetition().clearRecords();
            sender.sendMessage(Lang.CLEAR_RECORDS);
            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSender> end() {
        return literal("end").requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            CommandSender sender = context.getSource();
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition();
                if (!getConfig().getBoolean("messages.broadcast-stop", false)) {
                    sender.sendMessage(Lang.CONTEST_STOP);
                }
            }
            else {
                sender.sendMessage(Lang.ALREADY_STOPPED);
            }

            return 1;
        });
    }

    private static FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    private static FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    private static FishShop getFishShop() {
        return getPlugin().getFishShop();
    }

    private static MoreFish getPlugin() {
        return MoreFish.instance();
    }

    private static LiteralArgumentBuilder<CommandSender> help() {
        return literal("help").executes(MFCommands::help);
    }

    private static int help(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        PluginDescriptionFile pluginInfo = getPlugin().getDescription();
        String pluginName = pluginInfo.getName();
        String prefix = ChatColor.AQUA + "[" + pluginName + "]" + ChatColor.RESET + " ";
        sender.sendMessage(prefix + ChatColor.DARK_AQUA + "> ===== " + ChatColor.AQUA + ChatColor.BOLD + pluginName + ' ' + ChatColor.AQUA + 'v' + pluginInfo.getVersion() + ChatColor.DARK_AQUA + " ===== <");
        String label = prefix + "/mf";
        sender.sendMessage(label + " help");

        if (sender.hasPermission("morefish.admin")) {
            sender.sendMessage(label + " begin [runningTime(sec)]");
            sender.sendMessage(label + " suspend");
            sender.sendMessage(label + " end");
            sender.sendMessage(label + " rewards");
            sender.sendMessage(label + " clear");
            sender.sendMessage(label + " reload");
        }

        if (sender.hasPermission("morefish.top")) {
            sender.sendMessage(label + " top");
        }

        if (sender.hasPermission("morefish.shop") || sender.hasPermission("morefish.admin")) {
            sender.sendMessage(label + " shop" + (sender.hasPermission("morefish.admin") ? " [player]" : ""));
        }

        return 1;
    }

    static void registerCommands() {
        registerCommand(getPlugin(), literal("morefish").executes(MFCommands::help).then(begin("begin")).then(begin("start")).then(clear()).then(end()).then(help()).then(reload()).then(shop()).then(suspend()).then(top("top")).then(top("ranking")));
    }

    private static LiteralArgumentBuilder<CommandSender> reload() {
        return literal("reload").requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            CommandSender sender = context.getSource();
            try {
                getPlugin().applyConfig();
                sender.sendMessage(Lang.RELOAD_CONFIG);
            }
            catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Lang.FAILED_TO_RELOAD);
            }

            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSender> shop() {
        return literal("shop").requires(sender -> sender.hasPermission("morefish.shop") && sender instanceof Player).executes(context -> shop(context.getSource(), (Player) context.getSource())).then(argument("player", new PlayerArgumentType()).requires(sender -> sender.hasPermission("morefish.admin") && sender instanceof Player).executes(context -> shop(context.getSource(), context.getArgument("player", Player.class))));
    }

    private static int shop(CommandSender sender, Player guiUser) {
        if (!getFishShop().getEnabled()) {
            sender.sendMessage(Lang.SHOP_DISABLED);
        }
        else {
            getFishShop().openGuiTo(guiUser);
            if (!guiUser.getUniqueId().equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(Lang.replace(Lang.FORCED_PLAYER_TO_SHOP, Map.of("%s", guiUser.getName())));
            }
        }

        return 1;
    }

    private static LiteralArgumentBuilder<CommandSender> suspend() {
        return literal("suspend").requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            CommandSender sender = context.getSource();
            if (!getCompetition().isDisabled()) {
                getCompetitionHost().closeCompetition(true);
                if (!getConfig().getBoolean("messages.broadcast-stop", false)) {
                    sender.sendMessage(Lang.CONTEST_STOP);
                }
            }
            else {
                sender.sendMessage(Lang.ALREADY_STOPPED);
            }

            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSender> top(String name) {
        return literal(name).requires(sender -> sender.hasPermission("morefish.top")).executes(context -> {
            CommandSender sender = context.getSource();
            getCompetitionHost().informAboutRanking(sender);
            return 1;
        });
    }
}
