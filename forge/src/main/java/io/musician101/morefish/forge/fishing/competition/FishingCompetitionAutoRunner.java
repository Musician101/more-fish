package io.musician101.morefish.forge.fishing.competition;

import io.musician101.morefish.common.config.AutoRunningConfig;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.scheduler.Scheduler;
import io.musician101.morefish.forge.scheduler.Task;
import java.time.LocalTime;
import java.util.Collection;
import javax.annotation.Nonnull;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

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

        timeCheckingTask = new TimeChecker(this::tryOpenCompetition);
    }

    public final boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public final void setScheduledTimes(@Nonnull Collection<LocalTime> scheduledTimes) {
        this.scheduledTimes = scheduledTimes;
    }

    private void tryOpenCompetition() {
        ForgeMoreFish mod = ForgeMoreFish.getInstance();
        AutoRunningConfig autoRunningConfig = mod.getPluginConfig().getAutoRunningConfig();
        int requiredPlayers = autoRunningConfig.getRequiredPlayers();
        if (mod.getCompetition().isDisabled() && LogicalSidedProvider.INSTANCE.<MinecraftServer>get(LogicalSide.SERVER).getOnlinePlayerNames().length >= requiredPlayers) {
            long duration = autoRunningConfig.getTimer() * 20L;
            mod.getCompetitionHost().openCompetitionFor(duration);
        }
    }

    private final class TimeChecker extends Task {

        private final Runnable work;

        TimeChecker(@Nonnull Runnable work) {
            super(600);
            this.work = work;
        }

        public void run() {
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
            scheduledTimes.stream().filter(it -> it.equals(currentTime)).forEach(it -> work.run());
            Scheduler.scheduleTask(new TimeChecker(work));
        }
    }
}
