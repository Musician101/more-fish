package io.musician101.morefish.spigot.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.spigot.fishing.competition.SpigotPrize;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import javax.annotation.Nonnull;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

public final class MoreFishCommand {

    private MoreFishCommand() {

    }

    private static LiteralArgumentBuilder<Object> begin(@Nonnull Commodore commodore) {
        return redirect("start", literal("begin").requires(o -> commodore.getBukkitSender(o).hasPermission("morefish.admin")).then(requiredArgument("length", IntegerArgumentType.integer(getConfig().getAutoRunningConfig().getTimer())).executes(command -> {
            CommandSender sender = commodore.getBukkitSender(command.getSource());
            if (getCompetition().isDisabled()) {
                getCompetitionHost().openCompetitionFor(command.getArgument("length", Integer.class));
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

    private static LiteralArgumentBuilder<Object> clear(@Nonnull Commodore commodore) {
        return literal("clear").requires(o -> commodore.getBukkitSender(o).hasPermission("morefish.admin")).executes(command -> {
            getCompetition().clearRecords();
            commodore.getBukkitSender(command.getSource()).sendMessage(getLangConfig().text("clear-records"));
            return 1;
        });
    }

    private static LiteralArgumentBuilder<Object> end(@Nonnull Commodore commodore) {
        return literal("end").requires(o -> commodore.getBukkitSender(o).hasPermission("morefish.admin")).executes(command -> {
            CommandSender sender = commodore.getBukkitSender(command.getSource());
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

    private static FishingCompetition<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    private static Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> getConfig() {
        return getPlugin().getPluginConfig();
    }

    private static FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String> getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private static LangConfig<SpigotTextFormat, SpigotTextListFormat, String> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static MessagesConfig<SpigotPlayerAnnouncement, BarColor> getMessagesConfig() {
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
        sender.sendMessage(prefix + ChatColor.DARK_AQUA + "> ===== " +
                ChatColor.AQUA + ChatColor.BOLD + pluginName + ' ' +
                ChatColor.AQUA + 'v' + pluginInfo.getVersion() +
                ChatColor.DARK_AQUA + " ===== <");
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
        Commodore commodore = CommodoreProvider.getCommodore(SpigotMoreFish.getInstance());
        LiteralCommandNode<Object> command = literal("morefish").executes(o -> help(commodore.getBukkitSender(o.getSource()))).then(begin(commodore)).then(clear(commodore)).then(end(commodore)).then(reload(commodore)).then(shop(commodore)).then(suspend(commodore)).then(top(commodore)).build();
        commodore.register(redirect("fish", redirect("mf", command).build()));
    }

    private static LiteralArgumentBuilder<Object> literal(@Nonnull String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private static LiteralArgumentBuilder<Object> redirect(@Nonnull String name, @Nonnull LiteralCommandNode<Object> redirectTo) {
        return literal(name).redirect(redirectTo);
    }

    private static LiteralArgumentBuilder<Object> reload(@Nonnull Commodore commodore) {
        return literal("reload").requires(o -> commodore.getBukkitSender(o).hasPermission("morefish.admin")).executes(command -> {
            CommandSender sender = commodore.getBukkitSender(command.getSource());
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

    private static <T> RequiredArgumentBuilder<Object, T> requiredArgument(@Nonnull String name, @Nonnull ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }

    private static LiteralArgumentBuilder<Object> shop(@Nonnull Commodore commodore) {
        return literal("shop").then(requiredArgument("player", new PlayerArgumentType()).executes(command -> {
            CommandSender sender = commodore.getBukkitSender(command.getSource());
            Player guiUser = command.getArgument("player", Player.class);
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
                getFishShopConfig().openGuiTo(guiUser);
                if (!guiUser.getUniqueId().equals(((Player) sender).getUniqueId())) {
                    String msg = getLangConfig().format("forced-player-to-shop").replace(ImmutableMap.of("%s", guiUser.getName())).output();
                    sender.sendMessage(msg);
                }
            }

            return 1;
        }));
    }

    private static LiteralArgumentBuilder<Object> suspend(@Nonnull Commodore commodore) {
        return literal("suspend").requires(o -> commodore.getBukkitSender(o).hasPermission("morefish.admin")).executes(command -> {
            CommandSender sender = commodore.getBukkitSender(command.getSource());
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

    private static LiteralArgumentBuilder<Object> top(@Nonnull Commodore commodore) {
        return redirect("ranking", literal("top").requires(o -> commodore.getBukkitSender(o).hasPermission("morefish.top")).executes(command -> {
            getCompetitionHost().informAboutRanking(commodore.getBukkitSender(command.getSource()));
            return 1;
        }).build());
    }

}
