package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperArgumentCommand;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.command.argument.SortArgumentType;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.records.FishRecord;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
class MFFishingLogs implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private static final NodePath FISHING_LOGS_PATH = NodePath.path("command", "fishing-logs");

    private static void showRecords(CommandSourceStack source, int page, SortType sortType) {
        Player player = (Player) source.getSender();
        List<FishRecord> fullList = getPlugin().getFishingLogs().getFisher(player.getUniqueId());
        List<FishRecord> records = fullList.stream().skip((page - 1) * 8L).limit(8).sorted(sortType.reversed()).toList();
        if (records.isEmpty()) {
            player.sendMessage(lang().getComponent(FISHING_LOGS_PATH.withAppendedChild("none")));
            return;
        }

        records.forEach(record -> player.sendMessage(lang().getComponent(FISHING_LOGS_PATH.withAppendedChild("record"), record)));
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return lang().getComponent("command", "fishing_logs", "description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf fishinglogs [<page> [<sort>]]");
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new PageArgument());
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return isPlayer(source);
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        showRecords(context.getSource(), 1, SortType.TIMESTAMP);
        return 1;
    }

    @Override
    public String name() {
        return "fishinglogs";
    }

    static class PageArgument implements PaperArgumentCommand.AdventureFormat<Integer> {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            showRecords(context.getSource(), IntegerArgumentType.getInteger(context, name()), SortType.TIMESTAMP);
            return 1;
        }

        @Override
        public String name() {
            return "page";
        }

        @Override
        public ArgumentType<Integer> type() {
            return IntegerArgumentType.integer(1);
        }

        @Override
        public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
            return List.of(new SortArgument());
        }
    }

    static class SortArgument implements PaperArgumentCommand.AdventureFormat<SortType> {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            showRecords(context.getSource(), IntegerArgumentType.getInteger(context, "page"), SortArgumentType.getSortType(context, name()));
            return 1;
        }

        @Override
        public String name() {
            return "sort";
        }

        @Override
        public ArgumentType<SortType> type() {
            return new SortArgumentType();
        }
    }
}
