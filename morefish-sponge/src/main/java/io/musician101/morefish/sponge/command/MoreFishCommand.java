package io.musician101.morefish.sponge.command;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.sponge.fishing.competition.SpongePrize;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public final class MoreFishCommand {

    private MoreFishCommand() {

    }

    private static CommandCallable begin() {
        return CommandSpec.builder().permission("morefish.admin").arguments(GenericArguments.optional(GenericArguments.integer(Text.of("length")), getConfig().getAutoRunningConfig().getTimer())).executor((src, args) -> {
            if (getCompetition().isDisabled()) {
                //noinspection OptionalGetWithoutIsPresent
                getCompetitionHost().openCompetitionFor(args.<Integer>getOne("length").get());
                if (!getMessagesConfig().broadcastOnStart()) {
                    src.sendMessage(Text.of(getLangConfig().text("contest-start")));
                }
            }
            else {
                src.sendMessage(Text.of(getLangConfig().text("already-ongoing")));
            }

            return CommandResult.success();
        }).build();
    }

    private static CommandCallable clear() {
        return CommandSpec.builder().permission("morefish.admin").executor((src, args) -> {
            getCompetition().clearRecords();
            src.sendMessage(Text.of(getLangConfig().text("clear-records")));
            return CommandResult.success();
        }).build();
    }

    private static CommandCallable end() {
        return CommandSpec.builder().permission("morefish.admin").executor((src, args) -> {
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition();
                if (!getMessagesConfig().broadcastOnStop()) {
                    src.sendMessage(Text.of(getLangConfig().text("contest-stop")));
                }
            }
            else {
                src.sendMessage(Text.of(getLangConfig().text("already-stopped")));
            }

            return CommandResult.success();
        }).build();
    }

    private static FishingCompetition<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    private static Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> getConfig() {
        return getPlugin().getConfig();
    }

    private static FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text> getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private static LangConfig<SpongeTextFormat, SpongeTextListFormat, Text> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static MessagesConfig<SpongePlayerAnnouncement, BossBarColor> getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    private static SpongeMoreFish getPlugin() {
        return SpongeMoreFish.getInstance();
    }

    private static CommandResult help(@Nonnull CommandSource source) {
        if (source.hasPermission("morefish.help")) {
            source.sendMessage(Text.of(getLangConfig().text("no-permission")));
            return CommandResult.empty();
        }

        String prefix = TextColors.AQUA + "[" + Reference.NAME + "]" + TextColors.RESET + " ";
        source.sendMessage(Text.of(prefix, TextColors.DARK_AQUA, "> ===== ", TextColors.AQUA, TextStyles.BOLD, Reference.NAME + ' ', TextColors.AQUA, 'v' + Reference.VERSION, TextColors.DARK_AQUA, " ===== <"));
        String label = prefix + "/mf";
        source.sendMessage(Text.of(label + " help"));
        source.sendMessage(Text.of(label + " begin [runningTime(sec)]"));
        source.sendMessage(Text.of(label + " suspend"));
        source.sendMessage(Text.of(label + " end"));
        source.sendMessage(Text.of(label + " clear"));
        source.sendMessage(Text.of(label + " reload"));
        source.sendMessage(Text.of(label + " top"));
        source.sendMessage(Text.of(label + " shop [player]"));
        return CommandResult.success();
    }

    public static void init() {
        Sponge.getCommandManager().register(SpongeMoreFish.getInstance(), CommandSpec.builder().executor((src, args) -> help(src)).child(begin(), "begin", "start").child(clear(), "clear").child(end(), "end").child(reload(), "reload").child(shop(), "shop").child(suspend(), "suspend").child(top(), "ranking", "top").build(), "morefish", "mf", "fish");
    }

    private static CommandCallable reload() {
        return CommandSpec.builder().permission("morefish.admin").executor((src, args) -> {
            try {
                getPlugin().applyConfig();
                src.sendMessage(Text.of(getLangConfig().text("reload-config")));
            }
            catch (Exception e) {
                e.printStackTrace();
                src.sendMessage(Text.of(getLangConfig().text("failed-to-reload")));
            }

            return CommandResult.success();
        }).build();
    }

    private static CommandCallable shop() {
        return CommandSpec.builder().arguments(GenericArguments.optional(GenericArguments.requiringPermission(GenericArguments.player(Text.of("player")), "morefish.admin"))).executor((src, args) -> {
            Optional<Player> argument = args.getOne("player");
            Player guiUser;
            if (argument.isPresent()) {
                guiUser = argument.get();
            }
            else {
                if (!src.hasPermission("morefish.shop")) {
                    src.sendMessage(Text.of(getLangConfig().text("no-permission")));
                    return CommandResult.empty();
                }

                if (!(src instanceof Player)) {
                    src.sendMessage(Text.of(getLangConfig().text("in-game-command")));
                    return CommandResult.empty();
                }

                guiUser = (Player) src;
            }

            if (!getFishShopConfig().isEnabled()) {
                src.sendMessage(Text.of(getLangConfig().text("shop-disabled")));
            }
            else {
                getFishShopConfig().openGuiTo(guiUser);
                if (!guiUser.getUniqueId().equals(((Player) src).getUniqueId())) {
                    Text msg = getLangConfig().format("forced-player-to-shop").replace(ImmutableMap.of("%s", guiUser.getName())).output();
                    src.sendMessage(msg);
                }
            }

            return CommandResult.success();
        }).build();
    }

    private static CommandCallable suspend() {
        return CommandSpec.builder().permission("morefish.admin").executor((src, args) -> {
            if (getCompetition().isEnabled()) {
                getCompetitionHost().closeCompetition(true);
                if (!getMessagesConfig().broadcastOnStop()) {
                    src.sendMessage(Text.of(getLangConfig().text("contest-stop")));
                }
            }
            else {
                src.sendMessage(Text.of(getLangConfig().text("already-stopped")));
            }

            return CommandResult.success();
        }).build();
    }

    private static CommandCallable top() {
        return CommandSpec.builder().permission("morefish.top").executor((src, args) -> {
            getCompetitionHost().informAboutRanking(src);
            return CommandResult.success();
        }).build();
    }

}
