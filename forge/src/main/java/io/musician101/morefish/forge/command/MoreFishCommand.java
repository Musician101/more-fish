package io.musician101.morefish.forge.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.fishing.competition.FishingCompetitionHost;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public final class MoreFishCommand {

    private MoreFishCommand() {

    }

    private static LiteralArgumentBuilder<CommandSource> begin() {
        return redirect("start", literal("begin").requires(source -> source.hasPermissionLevel(3)).then(requiredArgument("length", IntegerArgumentType.integer(getConfig().getAutoRunningConfig().getTimer())).executes(context -> {
            CommandSource sender = context.getSource();
            if (getCompetition().isDisabled()) {
                getCompetitionHost().openCompetitionFor(context.getArgument("length", Integer.class));
                if (!getMessagesConfig().broadcastOnStart()) {
                    sender.sendFeedback(getLangConfig().text("contest-start"), true);
                }
            }
            else {
                sender.sendFeedback(getLangConfig().text("already-ongoing"), true);
            }

            return 1;
        })).build());
    }

    private static LiteralArgumentBuilder<CommandSource> clear() {
        return literal("clear").requires(source -> source.hasPermissionLevel(3)).executes(command -> {
            getCompetition().clearRecords();
            command.getSource().sendFeedback(getLangConfig().text("clear-records"), true);
            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSource> end() {
        return literal("end").requires(source -> source.hasPermissionLevel(3)).executes(command -> {
            CommandSource sender = command.getSource();
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition();
                if (!getMessagesConfig().broadcastOnStop()) {
                    sender.sendFeedback(getLangConfig().text("contest-stop"), true);
                }
            }
            else {
                sender.sendFeedback(getLangConfig().text("already-stopped"), true);
            }

            return 1;
        });
    }

    private static FishingCompetition<ItemStack> getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    private static Config getConfig() {
        return getPlugin().getPluginConfig();
    }

    private static FishShopConfig getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private static LangConfig<?, ?, ?> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static MessagesConfig<?, ?> getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    private static ForgeMoreFish getPlugin() {
        return ForgeMoreFish.getInstance();
    }

    private static int help(@Nonnull CommandSource sender) {
        StringTextComponent prefix = (StringTextComponent) new StringTextComponent("[" + Reference.ID + "]").mergeStyle(TextFormatting.AQUA).append(new StringTextComponent(" ").mergeStyle(TextFormatting.RESET));
        sender.sendFeedback(prefix.append(new StringTextComponent("> ===== ").mergeStyle(TextFormatting.DARK_AQUA).append(new StringTextComponent(Reference.ID + " v" + Reference.VERSION).mergeStyle(TextFormatting.AQUA, TextFormatting.BOLD).append(new StringTextComponent(" ===== <").mergeStyle(TextFormatting.AQUA)))), true);
        String label = prefix + "/mf";
        sender.sendFeedback(new StringTextComponent(label + " help"), true);
        sender.sendFeedback(new StringTextComponent(label + " begin [runningTime(sec)]"), true);
        sender.sendFeedback(new StringTextComponent(label + " suspend"), true);
        sender.sendFeedback(new StringTextComponent(label + " end"), true);
        sender.sendFeedback(new StringTextComponent(label + " clear"), true);
        sender.sendFeedback(new StringTextComponent(label + " reload"), true);
        sender.sendFeedback(new StringTextComponent(label + " top"), true);
        sender.sendFeedback(new StringTextComponent(label + " shop [player]"), true);
        return 1;
    }

    public static void init() {
        LiteralCommandNode<CommandSource> test = literal("morefish").executes(context -> help(context.getSource())).then(begin()).then(clear()).then(end()).then(reload()).then(shop()).then(suspend()).then(top()).build();
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        server.getCommandManager().getDispatcher().register(redirect("fish", redirect("mf", test).build()));
    }

    private static LiteralArgumentBuilder<CommandSource> literal(@Nonnull String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private static LiteralArgumentBuilder<CommandSource> redirect(@Nonnull String name, @Nonnull LiteralCommandNode<CommandSource> redirectTo) {
        return literal(name).redirect(redirectTo);
    }

    private static LiteralArgumentBuilder<CommandSource> reload() {
        return literal("reload").requires(source -> source.hasPermissionLevel(3)).executes(command -> {
            CommandSource sender = command.getSource();
            try {
                getPlugin().applyConfig();
                sender.sendFeedback(getLangConfig().text("reload-config"), true);
            }
            catch (Exception e) {
                e.printStackTrace();
                sender.sendFeedback(getLangConfig().text("failed-to-reload"), true);
            }

            return 1;
        });
    }

    private static <T> RequiredArgumentBuilder<CommandSource, T> requiredArgument(@Nonnull String name, @Nonnull ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }

    private static LiteralArgumentBuilder<CommandSource> shop() {
        return literal("shop").then(requiredArgument("player", new PlayerArgumentType()).executes(command -> {
            CommandSource sender = command.getSource();
            ServerPlayerEntity guiUser = command.getArgument("player", ServerPlayerEntity.class);
            if (guiUser != null) {
                if (!sender.hasPermissionLevel(3)) {
                    sender.sendFeedback(getLangConfig().text("no-permission"), true);
                    return 0;
                }
            }
            else {
                guiUser = sender.asPlayer();
            }

            if (!getFishShopConfig().isEnabled()) {
                sender.sendFeedback(getLangConfig().text("shop-disabled"), true);
            }
            else {
                getFishShopConfig().openGuiTo(guiUser);
                if (!guiUser.getUniqueID().equals(sender.asPlayer().getUniqueID())) {
                    ITextComponent msg = getLangConfig().format("forced-player-to-shop").replace(ImmutableMap.of("%s", guiUser.getName())).output();
                    sender.sendFeedback(msg, true);
                }
            }

            return 1;
        }));
    }

    private static LiteralArgumentBuilder<CommandSource> suspend() {
        return literal("suspend").requires(o -> o.hasPermissionLevel(3)).executes(command -> {
            CommandSource sender = command.getSource();
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition(true);
                if (!getMessagesConfig().broadcastOnStop()) {
                    sender.sendFeedback(getLangConfig().text("contest-stop"), true);
                }
            }
            else {
                sender.sendFeedback(getLangConfig().text("already-stopped"), true);
            }

            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSource> top() {
        return redirect("ranking", literal("top").executes(command -> {
            getCompetitionHost().informAboutRanking(command.getSource());
            return 1;
        }).build());
    }

}
