package me.elsiff.morefish.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.lang.ArgumentUtil;
import me.elsiff.morefish.records.FishRecord;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishingCompetitionHost {

    private final FishingCompetitionTimerBarHandler timerBarHandler = new FishingCompetitionTimerBarHandler();
    @Nullable
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

        Bukkit.broadcast(Component.translatable("morefish.main.contest.stop"));
        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<FishRecord> ranking = getCompetition().getRecords();
                ranking.sort(SortType.LENGTH.reversed());
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            FishRecord record = ranking.get(place);
                            prize.giveTo(Bukkit.getOfflinePlayer(record.fisher()), getPlugin());
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

    public FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    private Map<Integer, Prize> getPrizes() {
        ConfigurationSection cs = getConfig().getConfigurationSection("contest.prizes");
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
        Bukkit.broadcast(Component.translatable("morefish.main.contest.start"));
        Bukkit.broadcast(Component.translatable("morefish.main.contest.timer", ArgumentUtil.timeRemaining(duration)));
    }
}
