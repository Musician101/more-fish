package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperArgumentCommand;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.command.argument.RecordsArgumentType;
import me.elsiff.morefish.command.argument.RecordsArgumentType.Holder;
import me.elsiff.morefish.records.FishRecord;
import me.elsiff.morefish.records.FishRecordKeeper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
class MFTop implements PaperLiteralCommand.AdventureFormat {

    private void informAboutRanking(CommandContext<CommandSourceStack> context, FishRecordKeeper recordKeeper, @Nullable List<FishRecord> records) {
        CommandSender sender = context.getSource().getSender();
        if (records != null) {
            recordKeeper.informAboutRanking(sender, records);
        }
        else {
            recordKeeper.informAboutRanking(sender);
        }
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return lang().getComponent("command", "top", "description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf top [alltime [<sort>]|competition]");
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        informAboutRanking(context, getPlugin().getCompetition(), null);
        return 1;
    }

    @Override
    public String name() {
        return "top";
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new AllTimeCommand(), new CompetitionCommand());
    }

    class AllTimeCommand implements PaperLiteralCommand.AdventureFormat {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            informAboutRanking(context, getPlugin().getFishingLogs(), null);
            return 1;
        }

        @Override
        public String name() {
            return "alltime";
        }

        @Override
        public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
            return List.of(new FishArgument());
        }

        class FishArgument implements PaperArgumentCommand.AdventureFormat<Holder> {

            @Override
            public ArgumentType<RecordsArgumentType.Holder> type() {
                return new RecordsArgumentType();
            }

            @Override
            public Integer execute(CommandContext<CommandSourceStack> context) {
                informAboutRanking(context, getPlugin().getFishingLogs(), RecordsArgumentType.getRecords(context, name()));
                return 1;
            }

            @Override
            public String name() {
                return "fish";
            }
        }
    }

    class CompetitionCommand implements PaperLiteralCommand.AdventureFormat {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            informAboutRanking(context, getPlugin().getCompetition(), null);
            return 1;
        }

        @Override
        public String name() {
            return "competition";
        }
    }
}
