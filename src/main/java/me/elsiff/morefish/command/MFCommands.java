package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import java.util.Map;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.FishBag;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.shop.FishShop;
import me.elsiff.morefish.shop.FishShopGui;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static io.musician101.bukkitier.Bukkitier.argument;
import static io.musician101.bukkitier.Bukkitier.literal;
import static io.musician101.bukkitier.Bukkitier.registerCommand;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

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

    static LiteralArgumentBuilder<CommandSender> contraband() {
        return literal("contraband").requires(sender -> sender instanceof Player).executes(context -> {
            Player player = (Player) context.getSource();
            FishBag fishBag = getPlugin().getFishBags().getFishBag(player);
            fishBag.getContraband().forEach(i -> {
                World world = player.getWorld();
                world.dropItem(player.getLocation(), i);
            });
            fishBag.clearContraband();
            player.sendMessage(text("[MF] All contraband has been dropped from your bag. Make sure you get it all.", GREEN));
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

    @SuppressWarnings("UnstableApiUsage")
    private static int help(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        PluginMeta pluginInfo = getPlugin().getPluginMeta();
        String pluginName = pluginInfo.getName();
        Component prefix = text("[" + pluginName + "] ", AQUA);
        sender.sendMessage(join(prefix, text("> ===== ", DARK_AQUA), text(pluginName + ' ', style(AQUA, BOLD)),  text('v' + pluginInfo.getVersion(), AQUA), text(" ===== <", DARK_AQUA)));
        Component label = join(prefix, text("/mf"));
        sender.sendMessage(join(label, text(" help")));

        if (sender.hasPermission("morefish.admin")) {
            sender.sendMessage(join(label, text(" begin [runningTime(sec)]")));
            sender.sendMessage(join(label, text(" suspend")));
            sender.sendMessage(join(label, text(" end")));
            sender.sendMessage(join(label, text(" rewards")));
            sender.sendMessage(join(label, text(" clear")));
            sender.sendMessage(join(label, text(" reload")));
        }

        if (sender.hasPermission("morefish.top")) {
            sender.sendMessage(join(label, text(" top")));
        }

        if (sender.hasPermission("morefish.shop") || sender.hasPermission("morefish.admin")) {
            sender.sendMessage(join(label, text(" shop"), text((sender.hasPermission("morefish.admin") ? " [player]" : ""))));
        }

        sender.sendMessage(join(label, text(" contraband")));
        return 1;
    }

    static void registerCommands() {
        registerCommand(getPlugin(), literal("morefish").executes(MFCommands::help).then(begin("begin")).then(begin("start")).then(clear()).then(contraband()).then(end()).then(help()).then(reload()).then(shop()).then(suspend()).then(top("top")).then(top("ranking")));
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
        if (!getFishShop().getEnabled() || !MoreFish.instance().getVault().hasEconomy()) {
            sender.sendMessage(Lang.SHOP_DISABLED);
        }
        else {
            new FishShopGui(guiUser, 1);
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
