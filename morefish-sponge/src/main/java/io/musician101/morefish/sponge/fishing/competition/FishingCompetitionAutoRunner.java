package io.musician101.morefish.sponge.fishing.competition;

import io.musician101.morefish.common.config.AutoRunningConfig;
import io.musician101.morefish.sponge.SpongeMoreFish;
import java.time.LocalTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

public final class FishingCompetitionAutoRunner {

    private Collection<LocalTime> scheduledTimes;
    private Task timeCheckingTask;

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

        timeCheckingTask = Task.builder().execute(new TimeChecker(this::tryOpenCompetition)).interval(30, TimeUnit.SECONDS).submit(SpongeMoreFish.getInstance());
    }

    public final boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public final void setScheduledTimes(@Nonnull Collection<LocalTime> scheduledTimes) {
        this.scheduledTimes = scheduledTimes;
    }

    private void tryOpenCompetition() {
        AutoRunningConfig autoRunningConfig = SpongeMoreFish.getInstance().getConfig().getAutoRunningConfig();
        int requiredPlayers = autoRunningConfig.getRequiredPlayers();
        if (SpongeMoreFish.getInstance().getCompetition().isDisabled() && Sponge.getServer().getOnlinePlayers().size() >= requiredPlayers) {
            long duration = autoRunningConfig.getTimer() * 20L;
            SpongeMoreFish.getInstance().getCompetitionHost().openCompetitionFor(duration);
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
