package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.command.argument.FishArgumentType;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.fishing.fishrecords.FishRecordKeeper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.raw;

class MFTop implements LiteralCommand {

    private static void informAboutRanking(CommandContext<CommandSender> context, FishRecordKeeper recordKeeper, List<FishRecord> records) {
        recordKeeper.informAboutRanking(context.getSource(), records);
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return raw("command-top-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf top [alltime [<sort>]|competition]";
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        informAboutRanking(context, getPlugin().getCompetition(), null);
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "top";
    }

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new AllTimeCommand(), new CompetitionCommand());
    }

    static class AllTimeCommand implements LiteralCommand {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            informAboutRanking(context, getPlugin().getFishingLogs(), null);
            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "alltime";
        }

        @Override
        public @NotNull List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
            return List.of(new FishArgument());
        }

        static class FishArgument implements ArgumentCommand<FishArgumentType.Holder> {

            @NotNull
            @Override
            public ArgumentType<FishArgumentType.Holder> type() {
                return new FishArgumentType();
            }

            @Override
            public int execute(@NotNull CommandContext<CommandSender> context) {
                informAboutRanking(context, getPlugin().getFishingLogs(), context.getArgument(name(), FishArgumentType.Holder.class).get());
                return 1;
            }

            @NotNull
            @Override
            public String name() {
                return "fish";
            }
        }
    }

    static class CompetitionCommand implements LiteralCommand {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            getPlugin().getCompetition().informAboutRanking(sender);
            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "competition";
        }
    }
}
