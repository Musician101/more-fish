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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;

public class MFClear extends MFCommand implements LiteralCommand {

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new RecordsArgument());
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return testAdmin(sender);
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        getCompetition().clear();
        sender.sendMessage(join(PREFIX, text("The records has been cleared successfully.")));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "clear";
    }

    public static class RecordsArgument extends MFCommand implements ArgumentCommand<FishRecordsType> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            FishRecordsType recordsType = context.getArgument(name(), FishRecordsType.class);
            if (recordsType == FishRecordsType.COMPETITION) {
                getCompetition().clear();
                sender.sendMessage(join(PREFIX, text("The records has been cleared successfully.")));
            }
            else if (recordsType == FishRecordsType.ALLTIME) {
                getFishingLogs().clear();
                sender.sendMessage(join(PREFIX, text("The all time records have been cleared successfully.")));
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

    static class FishRecordHolderArgument extends MFCommand implements ArgumentCommand<UUID> {

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
                sender.sendMessage(join(PREFIX, text("The records for " + name + " has been cleared successfully.")));
            }
            else if (recordsType == FishRecordsType.ALLTIME) {
                getFishingLogs().clearRecordHolder(uuid);
                sender.sendMessage(join(PREFIX, text("The all time records for" + name + " have been cleared successfully.")));
            }

            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "fisher";
        }

        @NotNull
        @Override
        public ArgumentType<UUID> type() {
            return new UUIDArgumentType();
        }
    }
}
