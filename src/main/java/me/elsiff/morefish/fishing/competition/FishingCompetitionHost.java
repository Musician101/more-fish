package me.elsiff.morefish.fishing.competition;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.util.NumberUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionHost {

    @Nonnull
    private final MoreFish plugin = MoreFish.instance();
    @Nonnull
    private final FishingCompetition competition = plugin.getCompetition();
    @Nonnull
    private final FishingCompetitionTimerBarHandler timerBarHandler = new FishingCompetitionTimerBarHandler();
    private BukkitTask timerTask;

    public void closeCompetition() {
        closeCompetition(false);
    }

    public void closeCompetition(boolean suspend) {
        competition.disable();
        if (timerTask != null) {
            timerTask.cancel();
            if (timerBarHandler.getHasTimerEnabled()) {
                timerBarHandler.disableTimer();
            }
        }

        boolean broadcast = getMsgConfig().getBoolean("broadcast-stop");
        if (broadcast) {
            plugin.getServer().broadcast(Component.text(Lang.CONTEST_STOP));
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<Record> ranking = competition.getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            Record record = ranking.get(place);
                            prize.giveTo(Bukkit.getOfflinePlayer(record.getFisher()), competition.rankNumberOf(record), plugin);
                        }
                    });
                }
            }

            if (broadcast && getMsgConfig().getBoolean("show-top-on-ending")) {
                plugin.getServer().getOnlinePlayers().forEach(this::informAboutRanking);
            }
        }

        if (!getConfig().getBoolean("general.save-records")) {
            competition.clearRecords();
        }
    }

    @Nonnull
    public FishingCompetition getCompetition() {
        return competition;
    }

    @Nonnull
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    private ConfigurationSection getMsgConfig() {
        return getConfig().getConfigurationSection("messages");
    }

    @Nonnull
    private Map<Integer, Prize> getPrizes() {
        return Config.getPrizes();
    }

    public void informAboutRanking(@Nonnull CommandSender receiver) {
        if (competition.getRanking().isEmpty()) {
            receiver.sendMessage(Lang.TOP_NO_RECORD);
        }
        else {
            int topSize = getMsgConfig().getInt("top-number", 1);
            List<Record> top = competition.top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(Lang.replace(Lang.TOP_LIST, topReplacementOf(number, record)));
            });

            if (receiver instanceof Player) {
                if (!competition.containsContestant(((Player) receiver).getUniqueId())) {
                    receiver.sendMessage(Lang.TOP_MINE_NO_RECORD);
                }
                else {
                    Entry<Integer, Record> entry = competition.rankedRecordOf((OfflinePlayer) receiver);
                    receiver.sendMessage(Lang.replace(Lang.TOP_MINE, topReplacementOf(entry.getKey() + 1, entry.getValue())));
                }
            }
        }

    }

    public void openCompetition() {
        competition.enable();
        if (getMsgConfig().getBoolean("broadcast-start")) {
            plugin.getServer().broadcast(Component.text(Lang.CONTEST_START));
        }

    }

    public void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        competition.enable();
        Server server = plugin.getServer();
        timerTask = server.getScheduler().runTaskLater(plugin, (Runnable) this::closeCompetition, tick);
        if (getConfig().getBoolean("general.use-boss-bar")) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMsgConfig().getBoolean("broadcast-start")) {
            server.broadcast(Component.text(Lang.CONTEST_START));
            server.broadcast(Component.text(Lang.replace(Lang.CONTEST_START_TIMER, Map.of("%time%", Lang.time(duration)))));
        }
    }

    private Map<String, Object> topReplacementOf(int number, Record record) {
        String player = Bukkit.getOfflinePlayer(record.getFisher()).getName();
        return Map.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", player == null ? "null" : player, "%length%", String.valueOf(record.getFish().getLength()), "%fish%", record.getFish().getType().getName());
    }
}
