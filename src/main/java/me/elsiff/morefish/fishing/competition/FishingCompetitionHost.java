package me.elsiff.morefish.fishing.competition;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingCompetitionHost {

    @Nonnull
    private final FishingCompetitionTimerBarHandler timerBarHandler = new FishingCompetitionTimerBarHandler();
    private BukkitTask timerTask;

    public void closeCompetition() {
        closeCompetition(false);
    }

    public void closeCompetition(boolean suspend) {
        getCompetition().disable();
        if (timerTask != null) {
            timerTask.cancel();
            if (timerBarHandler.getHasTimerEnabled()) {
                timerBarHandler.disableTimer();
            }
        }

        boolean broadcast = getMsgConfig().getBoolean("broadcast-stop");
        if (broadcast) {
            Bukkit.broadcast(Lang.CONTEST_STOP);
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<Record> ranking = getCompetition().getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            Record record = ranking.get(place);
                            prize.giveTo(Bukkit.getOfflinePlayer(record.fisher()), getCompetition().rankNumberOf(record), getPlugin());
                        }
                    });
                }
            }

            if (broadcast && getMsgConfig().getBoolean("show-top-on-ending")) {
                Bukkit.getOnlinePlayers().forEach(this::informAboutRanking);
            }
        }

        if (!getConfig().getBoolean("general.save-records")) {
            getCompetition().clearRecords();
        }
    }

    @Nonnull
    public FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    @Nonnull
    private FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    private ConfigurationSection getMsgConfig() {
        return getConfig().getConfigurationSection("messages");
    }

    @Nonnull
    private Map<Integer, Prize> getPrizes() {
        return Config.getPrizes();
    }

    public void informAboutRanking(@Nonnull CommandSender receiver) {
        if (getCompetition().getRanking().isEmpty()) {
            receiver.sendMessage(Lang.TOP_NO_RECORD);
        }
        else {
            int topSize = getMsgConfig().getInt("top-number", 1);
            List<Record> top = getCompetition().top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(Lang.replace(Lang.TOP_LIST, topReplacementOf(number, record)));
            });

            if (receiver instanceof Player) {
                if (!getCompetition().containsContestant(((Player) receiver).getUniqueId())) {
                    receiver.sendMessage(Lang.TOP_MINE_NO_RECORD);
                }
                else {
                    Entry<Integer, Record> entry = getCompetition().rankedRecordOf((OfflinePlayer) receiver);
                    receiver.sendMessage(Lang.replace(Lang.TOP_MINE, topReplacementOf(entry.getKey() + 1, entry.getValue())));
                }
            }
        }

    }

    public void openCompetition() {
        getCompetition().enable();
        if (getMsgConfig().getBoolean("broadcast-start")) {
            Bukkit.broadcast(Lang.CONTEST_START);
        }

    }

    public void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Bukkit.getScheduler().runTaskLater(getPlugin(), (Runnable) this::closeCompetition, tick);
        if (getConfig().getBoolean("general.use-boss-bar")) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMsgConfig().getBoolean("broadcast-start")) {
            Bukkit.broadcast(Lang.CONTEST_START);
            Bukkit.broadcast(Lang.replace(Lang.CONTEST_START_TIMER, Map.of("%time%", Lang.time(duration))));
        }
    }

    private Map<String, Object> topReplacementOf(int number, Record record) {
        String player = Bukkit.getOfflinePlayer(record.fisher()).getName();
        return Map.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", player == null ? "null" : player, "%length%", String.valueOf(record.fish().length()), "%fish%", record.fish().type().name());
    }
}
