package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.command.argument.SortArgumentType;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.date;
import static me.elsiff.morefish.text.Lang.fishLength;
import static me.elsiff.morefish.text.Lang.fishName;
import static me.elsiff.morefish.text.Lang.raw;
import static me.elsiff.morefish.text.Lang.replace;
import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver;

class MFFishingLogs implements LiteralCommand {

    private static void showRecords(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSource();
        int page = 1;
        SortArgumentType.SortType sortType = SortType.TIMESTAMP;
        try {
            page = IntegerArgumentType.getInteger(context, "page");
        }
        catch (IllegalArgumentException ignored) {

        }

        int start = (page - 1) * 8;
        List<FishRecord> fullList = getPlugin().getFishingLogs().getFisher(player.getUniqueId());
        int end = Math.min(start + 8, fullList.size());
        List<FishRecord> records = new ArrayList<>(fullList.subList(start, end));
        if (records.isEmpty()) {
            player.sendMessage(replace("<mf-lang:command-fishinglogs-none>"));
            return;
        }

        records.sort(sortType.reversed());
        records.forEach(record -> player.sendMessage(replace("<mf-lang:command-fishing-logs-record>", resolver(fishName(record), fishLength(record), date(record)))));
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return raw("command-fishing-logs-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf fishinglogs [<page> [<sort>]]";
    }

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new PageArgument());
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        showRecords(context);
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "fishinglogs";
    }

    static class PageArgument implements ArgumentCommand<Integer> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            showRecords(context);
            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "page";
        }

        @NotNull
        @Override
        public ArgumentType<Integer> type() {
            return IntegerArgumentType.integer(1);
        }

        @Override
        public @NotNull List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
            return List.of(new SortArgument());
        }
    }

    static class SortArgument implements ArgumentCommand<SortType> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            showRecords(context);
            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "sort";
        }

        @NotNull
        @Override
        public ArgumentType<SortType> type() {
            return new SortArgumentType();
        }
    }
}
