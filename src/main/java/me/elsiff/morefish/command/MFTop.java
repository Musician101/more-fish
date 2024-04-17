package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class MFTop extends MFCommand implements LiteralCommand {

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

    static class AllTimeCommand extends MFCommand implements LiteralCommand {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            if (getCompetition().getRecords().isEmpty()) {
                sender.sendMessage(join(PREFIX, text("Nobody has caught anything yet.")));
            }
            else {
                int topSize = 1;
                ConfigurationSection msg = getPlugin().getConfig().getConfigurationSection("messages");
                if (msg != null) {
                    topSize = msg.getInt("top-number", 1);
                }

                List<FishRecord> top = getFishingLogs().top(topSize);
                top.forEach(record -> {
                    int number = top.indexOf(record) + 1;
                    sender.sendMessage(Lang.replace(join(PREFIX, text("%ordinal%. ", YELLOW), text(": %player%, %length%cm %fish%", DARK_GRAY)), topReplacementOf(number, record)));
                });

                if (sender instanceof Player) {
                    if (!getCompetition().containsContestant(((Player) sender).getUniqueId())) {
                        sender.sendMessage(join(PREFIX, text("You haven't caught any fish.")));
                    }
                    else {
                        Map.Entry<Integer, FishRecord> entry = getCompetition().rankedRecordOf((OfflinePlayer) sender);
                        sender.sendMessage(Lang.replace(join(PREFIX, text("You're %ordinal%: %length%cm %fish%")), topReplacementOf(entry.getKey() + 1, entry.getValue())));
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

        private Map<String, Object> topReplacementOf(int number, FishRecord record) {
            String player = Bukkit.getOfflinePlayer(record.fisher()).getName();
            return Map.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", player == null ? "null" : player, "%length%", String.valueOf(record.getLength()), "%fish%", record.getFishName());
        }
    }

    static class CompetitionCommand extends MFCommand implements LiteralCommand {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            getCompetitionHost().informAboutRanking(sender);
            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "current";
        }
    }
}
