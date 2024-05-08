package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.command.argument.FishArgumentType;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.fishing.fishrecords.FishingLogs;
import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.PREFIX_COMPONENT;
import static me.elsiff.morefish.text.Lang.join;
import static me.elsiff.morefish.text.Lang.replace;
import static net.kyori.adventure.text.Component.text;

class MFTop implements LiteralCommand {

    public static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    public static FishingLogs getFishingLogs() {
        return getPlugin().getFishingLogs();
    }

    private static Map<String, Object> topReplacementOf(int number, FishRecord record) {
        String player = Bukkit.getOfflinePlayer(record.fisher()).getName();
        return Map.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", player == null ? "null" : player, "%length%", String.valueOf(record.getLength()), "%fish%", record.getFishName());
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return "Show the top catches.";
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf top [alltime [<sort>]|competition]";
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        getCompetitionHost().informAboutRanking(sender);
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
            CommandSender sender = context.getSource();
            if (getFishingLogs().getRecords().isEmpty()) {
                sender.sendMessage(join(PREFIX_COMPONENT, text("Nobody has caught anything yet.")));
            }
            else {
                int topSize = 1;
                ConfigurationSection msg = getPlugin().getConfig().getConfigurationSection("messages");
                if (msg != null) {
                    topSize = msg.getInt("top-number", 1);
                }

                List<FishRecord> top = getPlugin().getFishingLogs().top(topSize);
                top.forEach(record -> {
                    int number = top.indexOf(record) + 1;
                    sender.sendMessage(join(PREFIX_COMPONENT, replace("<yellow>%ordinal%. : <dark_gray>%player%, %length%cm %fish%", topReplacementOf(number, record))));
                });

                if (sender instanceof Player player) {
                    if (getFishingLogs().contains(player.getUniqueId())) {
                        Map.Entry<Integer, FishRecord> entry = getFishingLogs().rankedRecordOf(player);
                        sender.sendMessage(join(PREFIX_COMPONENT, replace("You're %ordinal%: %length%cm %fish%", topReplacementOf(entry.getKey() + 1, entry.getValue()))));
                    }
                    else {
                        sender.sendMessage(join(PREFIX_COMPONENT, text("You haven't caught any fish.")));
                    }
                }
            }

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

        static class FishArgument implements ArgumentCommand<List<FishRecord>> {

            @NotNull
            @Override
            public ArgumentType<List<FishRecord>> type() {
                return new FishArgumentType();
            }

            @Override
            public int execute(@NotNull CommandContext<CommandSender> context) throws CommandSyntaxException {
                CommandSender sender = context.getSource();
                List<FishRecord> records = context.getArgument(name(), List.class);
                if (getFishingLogs().getRecords().isEmpty()) {
                    sender.sendMessage(join(PREFIX_COMPONENT, text("Nobody has caught anything yet.")));
                }
                else {
                    int topSize = 1;
                    ConfigurationSection msg = getPlugin().getConfig().getConfigurationSection("messages");
                    if (msg != null) {
                        topSize = msg.getInt("top-number", 1);
                    }

                    records.sort(SortType.LENGTH.sorter().reversed());
                    List<FishRecord> top = records.subList(0, Math.min(topSize, records.size()));
                    top.forEach(record -> {
                        int number = top.indexOf(record) + 1;
                        sender.sendMessage(join(PREFIX_COMPONENT, replace("<yellow>%ordinal%. : <dark_gray>%player%, %length%cm %fish%", topReplacementOf(number, record))));
                    });

                    if (sender instanceof Player player) {
                        if (records.stream().anyMatch(r -> r.fisher().equals(player.getUniqueId()))) {
                            Map.Entry<Integer, FishRecord> entry = rankedRecordOf(records, player);
                            sender.sendMessage(join(PREFIX_COMPONENT, replace("You're %ordinal%: %length%cm %fish%", topReplacementOf(entry.getKey() + 1, entry.getValue()))));
                        }
                        else {
                            sender.sendMessage(join(PREFIX_COMPONENT, text("You haven't caught any fish.")));
                        }
                    }
                }

                return 1;
            }

            @NotNull
            private Map.Entry<Integer, FishRecord> rankedRecordOf(List<FishRecord> records, OfflinePlayer contestant) {
                records.sort(SortType.LENGTH.sorter().reversed());
                int place = 0;
                for (FishRecord record : records) {
                    if (record.fisher().equals(contestant.getUniqueId())) {
                        return new SimpleEntry<>(place, record);
                    }

                    place++;
                }

                throw new IllegalStateException("Record not found");
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
            getCompetitionHost().informAboutRanking(sender);
            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "competition";
        }
    }
}
