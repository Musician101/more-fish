package me.elsiff.morefish.fishing.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.text.Lang;
import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.PREFIX_STRING;
import static me.elsiff.morefish.text.Lang.contestStartTimer;
import static me.elsiff.morefish.text.Lang.replace;

public final class FishingCompetitionHost {

    @NotNull
    private final FishingCompetitionTimerBarHandler timerBarHandler = new FishingCompetitionTimerBarHandler();
    private ScheduledTask timerTask;

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

        Bukkit.broadcast(Lang.CONTEST_STOP);
        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<FishRecord> ranking = getCompetition().getRecords();
                ranking.sort(SortType.LENGTH.sorter().reversed());
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            FishRecord record = ranking.get(place);
                            prize.giveTo(Bukkit.getOfflinePlayer(record.fisher()), getCompetition().rankNumberOf(record), getPlugin());
                        }
                    });
                }
            }

            Bukkit.getOnlinePlayers().forEach(this::informAboutRanking);
        }

        if (getConfig().getBoolean("general.save-records")) {
            getCompetition().getRecords().forEach(r -> getPlugin().getFishingLogs().add(r));
        }

        getCompetition().clear();
    }

    @NotNull
    public FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    @NotNull
    private FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    private ConfigurationSection getMsgConfig() {
        return getConfig().getConfigurationSection("messages");
    }

    @NotNull
    private Map<Integer, Prize> getPrizes() {
        ConfigurationSection cs = getConfig().getConfigurationSection("contest-prizes");
        if (cs == null) {
            return Map.of();
        }

        return cs.getKeys(false).stream().collect(Collectors.toMap(key -> Integer.parseInt(key) - 1, key -> new Prize(cs.getStringList(key))));
    }

    public void informAboutRanking(@NotNull CommandSender receiver) {
        if (getCompetition().getRecords().isEmpty()) {
            receiver.sendMessage(replace(PREFIX_STRING + "<white>Nobody has caught anything yet."));
        }
        else {
            int topSize = getMsgConfig().getInt("top-number", 1);
            List<FishRecord> top = new ArrayList<>(getCompetition().top(topSize));
            top.sort(SortType.LENGTH.sorter().reversed());
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                String player = Bukkit.getOfflinePlayer(record.fisher()).getName();
                receiver.sendMessage(replace(PREFIX_STRING + "<yellow>" + NumberUtils.ordinalOf(number) + ". : <dark_gray>" + (player == null ? "null" : player) + ", " + record.getLength() + "cm " + record.getFishName()));
            });

            if (receiver instanceof Player player) {
                if (getCompetition().contains(player.getUniqueId())) {
                    Entry<Integer, FishRecord> entry = getCompetition().rankedRecordOf(player);
                    FishRecord record = entry.getValue();
                    receiver.sendMessage(replace(PREFIX_STRING + "<white>You're " + NumberUtils.ordinalOf(entry.getKey() + 1) + ": " + record.getLength() + "cm " + record.getFishName()));
                }
                else {
                    receiver.sendMessage(replace(PREFIX_STRING + "<white>You didn't catch any fish."));
                }
            }
        }
    }

    public void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Bukkit.getGlobalRegionScheduler().runDelayed(getPlugin(), task -> closeCompetition(), tick);
        timerBarHandler.enableTimer(duration);
        Bukkit.broadcast(Lang.CONTEST_START);
        Bukkit.broadcast(contestStartTimer(duration));
    }
}
