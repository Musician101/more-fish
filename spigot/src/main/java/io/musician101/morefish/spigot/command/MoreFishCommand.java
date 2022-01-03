package io.musician101.morefish.spigot.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.musician101.bukkitier.Bukkitier;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.spigot.shop.FishShopGui;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import static io.musician101.bukkitier.Bukkitier.argument;
import static io.musician101.bukkitier.Bukkitier.literal;

public final class MoreFishCommand {

    private MoreFishCommand() {

    }

    private static LiteralArgumentBuilder<CommandSender> begin() {
        return redirect("start", literal("begin").requires(sender -> sender.hasPermission("morefish.admin")).then(argument("length", IntegerArgumentType.integer(1)).executes(context -> {
            CommandSender sender = context.getSource();
            if (getCompetition().isDisabled()) {
                int length = context.getArgument("length", Integer.class);
                if (length <= 0) {
                    length = getConfig().getAutoRunningConfig().getTimer();
                }

                getCompetitionHost().openCompetitionFor(length);
                if (!getMessagesConfig().broadcastOnStart()) {
                    sender.sendMessage(getLangConfig().text("contest-start"));
                }
            }
            else {
                sender.sendMessage(getLangConfig().text("already-ongoing"));
            }

            return 1;
        })).build());
    }

    private static LiteralArgumentBuilder<CommandSender> clear() {
        return literal("clear").requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            getCompetition().clearRecords();
            context.getSource().sendMessage(getLangConfig().text("clear-records"));
            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSender> end() {
        return literal("end").requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            CommandSender sender = context.getSource();
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition();
                if (!getMessagesConfig().broadcastOnStop()) {
                    sender.sendMessage(getLangConfig().text("contest-stop"));
                }
            }
            else {
                sender.sendMessage(getLangConfig().text("already-stopped"));
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

    private static Config<SpigotTextFormat, SpigotTextListFormat, String> getConfig() {
        return getPlugin().getPluginConfig();
    }

    private static FishShopConfig getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private static LangConfig<SpigotTextFormat, SpigotTextListFormat, String> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static MessagesConfig getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    private static SpigotMoreFish getPlugin() {
        return SpigotMoreFish.getInstance();
    }

    private static int help(@Nonnull CommandSender sender) {
        if (sender.hasPermission("morefish.help")) {
            sender.sendMessage(getLangConfig().text("no-permission"));
            return 0;
        }

        PluginDescriptionFile pluginInfo = getPlugin().getDescription();
        String pluginName = pluginInfo.getName();
        String prefix = ChatColor.AQUA + "[" + pluginName + "]" + ChatColor.RESET + " ";
        sender.sendMessage(prefix + ChatColor.DARK_AQUA + "> ===== " + ChatColor.AQUA + ChatColor.BOLD + pluginName + ' ' + ChatColor.AQUA + 'v' + pluginInfo.getVersion() + ChatColor.DARK_AQUA + " ===== <");
        String label = prefix + "/mf";
        sender.sendMessage(label + " help");
        sender.sendMessage(label + " begin [runningTime(sec)]");
        sender.sendMessage(label + " suspend");
        sender.sendMessage(label + " end");
        sender.sendMessage(label + " clear");
        sender.sendMessage(label + " reload");
        sender.sendMessage(label + " top");
        sender.sendMessage(label + " shop [player]");
        return 1;
    }

    public static void init() {
        LiteralCommandNode<CommandSender> command = literal("morefish").executes(context -> help(context.getSource())).then(begin()).then(clear()).then(end()).then(reload()).then(shop()).then(suspend()).then(top()).build();
        Bukkitier.registerCommand(SpigotMoreFish.getInstance(), redirect("fish", redirect("mf", command).build()));
    }

    private static LiteralArgumentBuilder<CommandSender> redirect(@Nonnull String name, @Nonnull LiteralCommandNode<CommandSender> redirectTo) {
        return literal(name).redirect(redirectTo);
    }

    private static LiteralArgumentBuilder<CommandSender> reload() {
        return literal("reload").requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            CommandSender sender = context.getSource();
            try {
                getPlugin().applyConfig();
                sender.sendMessage(getLangConfig().text("reload-config"));
            }
            catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(getLangConfig().text("failed-to-reload"));
            }

            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSender> shop() {
        return literal("shop").then(argument("player", new PlayerArgumentType()).executes(context -> {
            CommandSender sender = context.getSource();
            Player guiUser = context.getArgument("player", Player.class);
            if (guiUser != null) {
                if (!sender.hasPermission("morefish.admin")) {
                    sender.sendMessage(getLangConfig().text("no-permission"));
                    return 0;
                }
            }
            else {
                if (!sender.hasPermission("morefish.shop")) {
                    sender.sendMessage(getLangConfig().text("no-permission"));
                    return 0;
                }

                if (!(sender instanceof Player)) {
                    sender.sendMessage(getLangConfig().text("in-game-command"));
                    return 0;
                }

                guiUser = (Player) sender;
            }

            if (!getFishShopConfig().isEnabled()) {
                sender.sendMessage(getLangConfig().text("shop-disabled"));
            }
            else {
                new FishShopGui(guiUser);
                if (!guiUser.getUniqueId().equals(((Player) sender).getUniqueId())) {
                    String msg = getLangConfig().format("forced-player-to-shop").replace(ImmutableMap.of("%s", guiUser.getName())).output();
                    sender.sendMessage(msg);
                }
            }

            return 1;
        }));
    }

    private static LiteralArgumentBuilder<CommandSender> suspend() {
        return literal("suspend").requires(sender -> sender.hasPermission("morefish.admin")).executes(context -> {
            CommandSender sender = context.getSource();
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition(true);
                if (!getMessagesConfig().broadcastOnStop()) {
                    sender.sendMessage(getLangConfig().text("contest-stop"));
                }
            }
            else {
                sender.sendMessage(getLangConfig().text("already-stopped"));
            }

            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSender> top() {
        return redirect("ranking", literal("top").requires(sender -> sender.hasPermission("morefish.top")).executes(context -> {
            getCompetitionHost().informAboutRanking(context.getSource());
            return 1;
        }).build());
    }

}
