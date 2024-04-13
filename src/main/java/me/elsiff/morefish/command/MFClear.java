package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import me.elsiff.morefish.command.argument.RecordsArgumentType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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
        getCompetition().clearRecords();
        sender.sendMessage(join(PREFIX, text("The records has been cleared successfully.")));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "clear";
    }

    public enum RecordsType {
        ALLTIME,
        CURRENT;

        public static Optional<RecordsType> get(@NotNull String name) {
            return Arrays.stream(values()).filter(r -> name.equals(r.toString().toLowerCase())).findFirst();
        }
    }

    static class RecordsArgument extends MFCommand implements ArgumentCommand<RecordsType> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            RecordsType recordsType = context.getArgument(name(), RecordsType.class);
            if (recordsType == RecordsType.CURRENT) {
                getCompetition().clearRecords();
                sender.sendMessage(join(PREFIX, text("The records has been cleared successfully.")));
            }
            else if (recordsType == RecordsType.ALLTIME) {
                getAllTimeRecords().clear();
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
        public ArgumentType<RecordsType> type() {
            return new RecordsArgumentType();
        }
    }
}
