package io.musician101.morefish.spigot.fishing.competition;

import io.musician101.morefish.common.config.AutoRunningConfig;
import io.musician101.morefish.spigot.SpigotMoreFish;
import java.time.LocalTime;
import java.util.Collection;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionAutoRunner {

    private Collection<LocalTime> scheduledTimes;
    private BukkitTask timeCheckingTask;

    public final void disable() {
        if (timeCheckingTask == null) {
            throw new IllegalStateException("Auto runner must not be disabled");
        }

        timeCheckingTask.cancel();
        timeCheckingTask = null;
    }

    public final void enable() {
        if (timeCheckingTask != null) {
            throw new IllegalStateException("Auto runner must not be already enabled");
        }

        timeCheckingTask = new TimeChecker(this::tryOpenCompetition).runTaskTimer(SpigotMoreFish.getInstance(), 0, 600L);
    }

    public final boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public final void setScheduledTimes(@Nonnull Collection<LocalTime> scheduledTimes) {
        this.scheduledTimes = scheduledTimes;
    }

    private void tryOpenCompetition() {
        AutoRunningConfig autoRunningConfig = SpigotMoreFish.getInstance().getPluginConfig().getAutoRunningConfig();
        int requiredPlayers = autoRunningConfig.getRequiredPlayers();
        if (SpigotMoreFish.getInstance().getCompetition().isDisabled() && Bukkit.getOnlinePlayers().size() >= requiredPlayers) {
            long duration = autoRunningConfig.getTimer() * 20L;
            SpigotMoreFish.getInstance().getCompetitionHost().openCompetitionFor(duration);
        }
    }

    private final class TimeChecker extends BukkitRunnable {

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
