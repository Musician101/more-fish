package me.elsiff.morefish.fishing.competition;

import java.time.LocalTime;
import java.util.Collection;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionAutoRunner {

    private static final long HALF_MINUTE = 600L;
    private final MoreFish plugin = MoreFish.instance();
    private final FishingCompetitionHost competitionHost = plugin.getCompetitionHost();
    private Collection<LocalTime> scheduledTimes;
    private BukkitTask timeCheckingTask;

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

        timeCheckingTask = new TimeChecker(this::tryOpenCompetition).runTaskTimer(plugin, 0, HALF_MINUTE);
    }

    public boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public void setScheduledTimes(@Nonnull Collection<LocalTime> scheduledTimes) {
        this.scheduledTimes = scheduledTimes;
    }

    private void tryOpenCompetition() {
        FileConfiguration config = plugin.getConfig();
        int requiredPlayers = config.getInt("auto-running.required-players");
        if (competitionHost.getCompetition().isDisabled() && plugin.getServer().getOnlinePlayers().size() >= requiredPlayers) {
            long duration = config.getLong("auto-running.timer") * 20L;
            competitionHost.openCompetitionFor(duration);
        }

    }

    private final class TimeChecker extends BukkitRunnable {

        private final Runnable work;

        TimeChecker(@Nonnull Runnable work) {
            super();
            this.work = work;
        }

        public void run() {
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
            scheduledTimes.stream().filter(it -> it.equals(currentTime)).forEach(it -> work.run());
        }
    }
}
