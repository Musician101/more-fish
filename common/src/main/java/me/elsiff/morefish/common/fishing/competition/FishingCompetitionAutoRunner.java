package me.elsiff.morefish.common.fishing.competition;

import java.time.LocalTime;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public abstract class FishingCompetitionAutoRunner<T> {

    protected static final long HALF_MINUTE = 600L;
    protected Collection<LocalTime> scheduledTimes;
    protected T timeCheckingTask;

    public abstract void disable();

    public abstract void enable();

    public boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public void setScheduledTimes(@NotNull Collection<LocalTime> scheduledTimes) {
        this.scheduledTimes = scheduledTimes;
    }

    protected abstract void tryOpenCompetition();
}
