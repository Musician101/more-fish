package me.elsiff.morefish.fishing.competition;

import java.time.LocalTime;
import java.util.Collection;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Config;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionAutoRunner {

    private static final long HALF_MINUTE = 600L;
    private final FishingCompetitionHost competitionHost;
    private final Plugin plugin;
    private Collection<LocalTime> scheduledTimes;
    private BukkitTask timeCheckingTask;

    public FishingCompetitionAutoRunner(@Nonnull Plugin plugin, @Nonnull FishingCompetitionHost competitionHost) {
        super();
        this.plugin = plugin;
        this.competitionHost = competitionHost;
    }

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

        timeCheckingTask = new TimeChecker(this::tryOpenCompetition).runTaskTimer(plugin, 0, HALF_MINUTE);
    }

    public final boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public final void setScheduledTimes(@Nonnull Collection<LocalTime> scheduledTimes) {
        this.scheduledTimes = scheduledTimes;
    }

    private void tryOpenCompetition() {
        int requiredPlayers = Config.INSTANCE.getStandard().getInt("auto-running.required-players");
        if (!competitionHost.getCompetition().isEnabled() && plugin.getServer().getOnlinePlayers().size() >= requiredPlayers) {
            long duration = Config.INSTANCE.getStandard().getLong("auto-running.timer") * 20L;
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
            scheduledTimes.stream().filter(it -> it == currentTime).forEach(it -> work.run());
        }
    }
}
