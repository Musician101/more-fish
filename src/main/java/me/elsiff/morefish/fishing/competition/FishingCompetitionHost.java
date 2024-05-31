package me.elsiff.morefish.fishing.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.text.Lang;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

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

        Bukkit.broadcast(Lang.replace("<mf-lang:contest-stop>"));
        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<FishRecord> ranking = getCompetition().getRecords();
                ranking.sort(SortType.LENGTH.reversed());
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            FishRecord record = ranking.get(place);
                            prize.giveTo(Bukkit.getOfflinePlayer(record.fisher()), getCompetition().rankNumberOf(record), getPlugin());
                        }
                    });
                }
            }

            Bukkit.getOnlinePlayers().forEach(getPlugin().getCompetition()::informAboutRanking);
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

    @NotNull
    private Map<Integer, Prize> getPrizes() {
        ConfigurationSection cs = getConfig().getConfigurationSection("contest-prizes");
        if (cs == null) {
            return Map.of();
        }

        return cs.getKeys(false).stream().collect(Collectors.toMap(key -> Integer.parseInt(key) - 1, key -> new Prize(cs.getStringList(key))));
    }

    public void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Bukkit.getAsyncScheduler().runDelayed(getPlugin(), task -> closeCompetition(), duration, TimeUnit.SECONDS);
        timerBarHandler.enableTimer(duration);
        Bukkit.broadcast(Lang.replace("<mf-lang:contest-start>"));
        Bukkit.broadcast(Lang.replace("<mf-lang:contest-start-timer>", Lang.timeRemaining(duration)));
    }
}
