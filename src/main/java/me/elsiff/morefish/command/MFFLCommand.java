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

import java.util.Date;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class MFFLCommand extends MFCommand implements LiteralCommand {

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
        int end = start + 8;
        List<FishRecord> records = getPlugin().getFishingLogs().getFisher(player.getUniqueId()).subList(start, end);
        if (records.isEmpty()) {
            player.sendMessage(join(PREFIX, text("You have not caught any fish yet.")));
            return;
        }

        records.sort(sortType.sorter());
        records.forEach(record -> {
            Date date = new Date(record.timestamp());
            player.sendMessage(join(PREFIX, text(record.getLength() + "cm " + record.getFishName(), DARK_GRAY), text(" " + date, YELLOW)));
        });
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

    static class PageArgument extends MFCommand implements ArgumentCommand<Integer> {

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

    static class SortArgument extends MFCommand implements ArgumentCommand<SortType> {

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
