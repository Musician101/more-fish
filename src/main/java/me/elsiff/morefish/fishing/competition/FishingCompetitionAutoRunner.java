package me.elsiff.morefish.fishing.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.time.LocalTime;
import java.util.Collection;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public final class FishingCompetitionAutoRunner {

    private static final long HALF_MINUTE = 600L;

    private MoreFish getPlugin() {
        return MoreFish.instance();
    }
    private FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }
    private Collection<LocalTime> scheduledTimes;
    private ScheduledTask timeCheckingTask;

    public void disable() {
        if (timeCheckingTask == null) {
            throw new IllegalStateException("Auto runner must not be disabled");
        }

        timeCheckingTask.cancel();
        timeCheckingTask = null;
    }

    public void enable() {
        if (timeCheckingTask != null) {
            throw new IllegalStateException("Auto runner must not be already enabled");
        }

        timeCheckingTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(getPlugin(), task -> new TimeChecker(this::tryOpenCompetition).run(), 1, HALF_MINUTE);
    }

    public boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public void setScheduledTimes(@Nonnull Collection<LocalTime> scheduledTimes) {
        this.scheduledTimes = scheduledTimes;
    }

    private void tryOpenCompetition() {
        FileConfiguration config = getPlugin().getConfig();
        int requiredPlayers = config.getInt("auto-running.required-players");
        if (getCompetitionHost().getCompetition().isDisabled() && Bukkit.getOnlinePlayers().size() >= requiredPlayers) {
            long duration = config.getLong("auto-running.timer") * 20L;
            getCompetitionHost().openCompetitionFor(duration);
        }

    }

    private final class TimeChecker implements Runnable {

        private final Runnable work;

        TimeChecker(@Nonnull Runnable work) {
            this.work = work;
        }

        public void run() {
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
            scheduledTimes.stream().filter(it -> it.equals(currentTime)).forEach(it -> work.run());
        }
    }
}
