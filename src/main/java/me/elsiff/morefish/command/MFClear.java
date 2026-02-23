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
import me.elsiff.morefish.lang.ArgumentUtil;
import me.elsiff.morefish.records.FishingLogs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
class MFClear implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private void clearAll(CommandSourceStack source, FishRecordsType recordsType) {
        if (recordsType == FishRecordsType.COMPETITION) {
            getCompetition().clear();
            sendMessage(source, Component.translatable("morefish.command.clear.competition.player-success"));
        }
        else if (recordsType == FishRecordsType.ALLTIME) {
            getFishingLogs().clear();
            sendMessage(source, Component.translatable("morefish.command.clear.alltime.success"));
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
        return Component.translatable("morefish.command.clear.description");
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        clearAll(source, FishRecordsType.COMPETITION);
        getCompetition().clear();
        sendMessage(source, Component.translatable("morefish.command.clear.success"));
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
                sendMessage(source, Component.translatable("morefish.command.clear.competition.player-success", ArgumentUtil.player(player)));
            }
            else if (recordsType == FishRecordsType.ALLTIME) {
                getFishingLogs().clearRecordHolder(uuid);
                sendMessage(source, Component.translatable("morefish.command.clear.alltime.player-success", ArgumentUtil.player(player)));
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
