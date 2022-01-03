package io.musician101.morefish.sponge.command;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.sponge.shop.FishShopGui;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;

public final class MoreFishCommand {

    private MoreFishCommand() {

    }

    private static Parameterized begin() {
        Parameter.Key<Integer> lengthKey = Parameter.key("length", Integer.class);
        return Command.builder().permission("morefish.admin").addParameter(Parameter.integerNumber().optional().key(lengthKey).build()).executor(context -> {
            if (getCompetition().isDisabled()) {
                getCompetitionHost().openCompetitionFor(context.one(lengthKey).map(length -> {
                    if (length <= 0) {
                        return getConfig().getAutoRunningConfig().getTimer();
                    }

                    return length;
                }).orElse(getConfig().getAutoRunningConfig().getTimer()));
                if (!getMessagesConfig().broadcastOnStart()) {
                    context.sendMessage(Identity.nil(), getLangConfig().text("contest-start"));
                }
            }
            else {
                context.sendMessage(Identity.nil(), getLangConfig().text("already-ongoing"));
            }

            return CommandResult.success();
        }).build();
    }

    private static Parameterized clear() {
        return Command.builder().permission("morefish.admin").executor(context -> {
            getCompetition().clearRecords();
            context.sendMessage(Identity.nil(), getLangConfig().text("clear-records"));
            return CommandResult.success();
        }).build();
    }

    private static Parameterized end() {
        return Command.builder().permission("morefish.admin").executor(context -> {
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition();
                if (!getMessagesConfig().broadcastOnStop()) {
                    context.sendMessage(Identity.nil(), getLangConfig().text("contest-stop"));
                }
            }
            else {
                context.sendMessage(Identity.nil(), getLangConfig().text("already-stopped"));
            }

            return CommandResult.success();
        }).build();
    }

    private static FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    private static Config<SpongeTextFormat, SpongeTextListFormat, Component> getConfig() {
        return getPlugin().getConfig();
    }

    private static FishShopConfig getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private static LangConfig<SpongeTextFormat, SpongeTextListFormat, Component> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static MessagesConfig getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    private static SpongeMoreFish getPlugin() {
        return SpongeMoreFish.getInstance();
    }

    private static CommandResult help(@Nonnull CommandContext source) {
        if (source.hasPermission("morefish.help")) {
            source.sendMessage(Identity.nil(), getLangConfig().text("no-permission"));
            return CommandResult.empty();
        }

        Component prefix = Component.text("[" + Reference.NAME + "]", NamedTextColor.AQUA).append(Component.text(" ", NamedTextColor.WHITE));
        source.sendMessage(Identity.nil(), Component.join(Component.text(), prefix, Component.text(), Component.text("> ===== ", NamedTextColor.DARK_AQUA), Component.text(Reference.NAME + " ", Style.style(NamedTextColor.AQUA, TextDecoration.BOLD)), Component.text('v' + Reference.VERSION, NamedTextColor.AQUA), Component.text(" ===== <", NamedTextColor.DARK_AQUA)));
        Component label = Component.join(Component.text(), prefix, Component.text("/mf"));
        source.sendMessage(Identity.nil(), Component.join(label, Component.text(" help")));
        source.sendMessage(Identity.nil(), Component.join(label, Component.text(" begin [runningTime(sec)]")));
        source.sendMessage(Identity.nil(), Component.join(label, Component.text(" suspend")));
        source.sendMessage(Identity.nil(), Component.join(label, Component.text(" end")));
        source.sendMessage(Identity.nil(), Component.join(label, Component.text(" clear")));
        source.sendMessage(Identity.nil(), Component.join(label, Component.text(" reload")));
        source.sendMessage(Identity.nil(), Component.join(label, Component.text(" top")));
        source.sendMessage(Identity.nil(), Component.join(label, Component.text(" shop [player]")));
        return CommandResult.success();
    }

    public static void init(@Nonnull RegisterCommandEvent<Command> event) {
        event.register(SpongeMoreFish.getInstance().getPluginContainer(), Command.builder().executor(MoreFishCommand::help).addChild(begin(), "begin", "start").addChild(clear(), "clear").addChild(end(), "end").addChild(reload(), "reload").addChild(shop(), "shop").addChild(suspend(), "suspend").addChild(top(), "ranking", "top").build(), "morefish", "mf", "fish");
    }

    private static Parameterized reload() {
        return Command.builder().permission("morefish.admin").executor(context -> {
            try {
                getPlugin().applyConfig();
                context.sendMessage(Identity.nil(), getLangConfig().text("reload-config"));
            }
            catch (Exception e) {
                e.printStackTrace();
                context.sendMessage(Identity.nil(), getLangConfig().text("failed-to-reload"));
            }

            return CommandResult.success();
        }).build();
    }

    private static Parameterized shop() {
        Parameter.Key<ServerPlayer> playerKey = Parameter.key("player", ServerPlayer.class);
        return Command.builder().addParameter(Parameter.player().key(playerKey).optional().requiredPermission("morefish.admin").build()).executor(context -> {
            Optional<ServerPlayer> argument = context.one(playerKey);
            ServerPlayer guiUser;
            if (argument.isPresent()) {
                guiUser = argument.get();
            }
            else {
                if (!context.hasPermission("morefish.shop")) {
                    context.sendMessage(Identity.nil(), getLangConfig().text("no-permission"));
                    return CommandResult.empty();
                }

                if (!(context.subject() instanceof ServerPlayer)) {
                    context.sendMessage(Identity.nil(), getLangConfig().text("in-game-command"));
                    return CommandResult.empty();
                }

                guiUser = (ServerPlayer) context.subject();
            }

            if (!getFishShopConfig().isEnabled()) {
                context.sendMessage(Identity.nil(), getLangConfig().text("shop-disabled"));
            }
            else {
                new FishShopGui(guiUser);
                if (!guiUser.uniqueId().equals(((ServerPlayer) context).uniqueId())) {
                    Component msg = getLangConfig().format("forced-player-to-shop").replace(ImmutableMap.of("%s", guiUser.name())).output();
                    context.sendMessage(Identity.nil(), msg);
                }
            }

            return CommandResult.success();
        }).build();
    }

    private static Parameterized suspend() {
        return Command.builder().permission("morefish.admin").executor(context -> {
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition(true);
                if (!getMessagesConfig().broadcastOnStop()) {
                    context.sendMessage(Identity.nil(), getLangConfig().text("contest-stop"));
                }
            }
            else {
                context.sendMessage(Identity.nil(), getLangConfig().text("already-stopped"));
            }

            return CommandResult.success();
        }).build();
    }

    private static Parameterized top() {
        return Command.builder().permission("morefish.top").executor(context -> {
            getCompetitionHost().informAboutRanking(context.cause().audience());
            return CommandResult.success();
        }).build();
    }

}
