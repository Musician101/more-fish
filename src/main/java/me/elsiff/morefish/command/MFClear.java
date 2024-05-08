package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.command.argument.FishRecordsTypeArgumentType;
import me.elsiff.morefish.command.argument.FishRecordsTypeArgumentType.FishRecordsType;
import me.elsiff.morefish.command.argument.UUIDArgumentType;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.fishrecords.FishingLogs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.PREFIX_STRING;
import static me.elsiff.morefish.text.Lang.replace;

class MFClear implements LiteralCommand {

    private static FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingLogs getFishingLogs() {
        return getPlugin().getFishingLogs();
    }

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new RecordsArgument());
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        getCompetition().clear();
        sender.sendMessage(replace(PREFIX_STRING + "<white>The records has been cleared successfully."));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "clear";
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return "Clears the records.";
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf clear [alltime|competition [<player>]]";
    }

    public static class RecordsArgument implements ArgumentCommand<FishRecordsType> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            FishRecordsType recordsType = context.getArgument(name(), FishRecordsType.class);
            if (recordsType == FishRecordsType.COMPETITION) {
                getCompetition().clear();
                sender.sendMessage(replace(PREFIX_STRING + "<white>The records has been cleared successfully."));
            }
            else if (recordsType == FishRecordsType.ALLTIME) {
                getFishingLogs().clear();
                sender.sendMessage(replace(PREFIX_STRING + "<white>The all time records have been cleared successfully."));
            }

            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "recordsType";
        }

        @NotNull
        @Override
        public ArgumentType<FishRecordsType> type() {
            return new FishRecordsTypeArgumentType();
        }

        @Override
        public @NotNull List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
            return List.of(new FishRecordHolderArgument());
        }
    }

    static class FishRecordHolderArgument implements ArgumentCommand<UUID> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            FishRecordsType recordsType = context.getArgument(name(), FishRecordsType.class);
            UUID uuid = context.getArgument(name(), UUID.class);
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name == null) {
                name = uuid.toString();
            }

            if (recordsType == FishRecordsType.COMPETITION) {
                getCompetition().clearRecordHolder(uuid);
                sender.sendMessage(replace(PREFIX_STRING + "<white>The records for " + name + " has been cleared successfully."));
            }
            else if (recordsType == FishRecordsType.ALLTIME) {
                getFishingLogs().clearRecordHolder(uuid);
                sender.sendMessage(replace(PREFIX_STRING + "<white>The all time records for" + name + " have been cleared successfully."));
            }

            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "player";
        }

        @NotNull
        @Override
        public ArgumentType<UUID> type() {
            return new UUIDArgumentType();
        }
    }
}
