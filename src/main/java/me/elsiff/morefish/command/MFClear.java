package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperArgumentCommand;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.command.argument.FishRecordsTypeArgumentType;
import me.elsiff.morefish.command.argument.FishRecordsTypeArgumentType.FishRecordsType;
import me.elsiff.morefish.command.argument.UUIDArgumentType;
import me.elsiff.morefish.competition.FishingCompetition;
import me.elsiff.morefish.lang.TagResolverUtil;
import me.elsiff.morefish.records.FishingLogs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.List;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
class MFClear implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private static final NodePath CLEAR_PATH = NodePath.path("command", "clear");
    private static final NodePath COMPETITION_PATH = CLEAR_PATH.withAppendedChild("competition");
    private static final NodePath ALL_TIME_PATH = CLEAR_PATH.withAppendedChild("alltime");
    private static final NodePath SUCCESS_PATH = NodePath.path("success");
    private static final NodePath PLAYER_SUCCESS_PATH = NodePath.path("player").plus(SUCCESS_PATH);

    private void clearAll(CommandSourceStack source, FishRecordsType recordsType) {
        if (recordsType == FishRecordsType.COMPETITION) {
            getCompetition().clear();
            sendMessage(source, lang().getComponent(COMPETITION_PATH.plus(SUCCESS_PATH)));
        }
        else if (recordsType == FishRecordsType.ALLTIME) {
            getFishingLogs().clear();
            sendMessage(source, lang().getComponent(ALL_TIME_PATH.plus(SUCCESS_PATH)));
        }
    }

    private FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private FishingLogs getFishingLogs() {
        return getPlugin().getFishingLogs();
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new RecordsArgument());
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return hasPermission(source, "morefish.admin");
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return lang().getComponent(CLEAR_PATH.withAppendedChild("description"));
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        clearAll(source, FishRecordsType.COMPETITION);
        getCompetition().clear();
        sendMessage(source, lang().getComponent(CLEAR_PATH.plus(SUCCESS_PATH)));
        return 1;
    }

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf clear [alltime|competition [<player>]]");
    }

    @NullMarked
    public class RecordsArgument implements PaperArgumentCommand.AdventureFormat<FishRecordsType> {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            clearAll(context.getSource(), FishRecordsTypeArgumentType.getRecordType(context, name()));
            return 1;
        }

        @Override
        public String name() {
            return "recordsType";
        }

        @Override
        public ArgumentType<FishRecordsType> type() {
            return new FishRecordsTypeArgumentType();
        }

        @Override
        public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
            return List.of(new FishRecordHolderArgument());
        }
    }

    class FishRecordHolderArgument implements PaperArgumentCommand.AdventureFormat<UUID> {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            CommandSourceStack source = context.getSource();
            FishRecordsType recordsType = FishRecordsTypeArgumentType.getRecordType(context, "recordsType");
            UUID uuid = UUIDArgumentType.getUUID(context, name());
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (recordsType == FishRecordsType.COMPETITION) {
                getCompetition().clearRecordHolder(uuid);
                sendMessage(source, lang().getComponent(COMPETITION_PATH.plus(PLAYER_SUCCESS_PATH), TagResolverUtil.playerNameResolver(player)));
            }
            else if (recordsType == FishRecordsType.ALLTIME) {
                getFishingLogs().clearRecordHolder(uuid);
                sendMessage(source, lang().getComponent(ALL_TIME_PATH.plus(PLAYER_SUCCESS_PATH), TagResolverUtil.playerNameResolver(player)));
            }

            return 1;
        }

        @Override
        public String name() {
            return "player";
        }

        @Override
        public ArgumentType<UUID> type() {
            return new UUIDArgumentType();
        }
    }
}
