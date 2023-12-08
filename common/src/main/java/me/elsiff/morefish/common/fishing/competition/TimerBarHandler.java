package me.elsiff.morefish.common.fishing.competition;

import net.kyori.adventure.bossbar.BossBar;

public abstract class TimerBarHandler<T> {

    protected T barUpdatingTask;
    protected BossBar timerBar;

    public abstract void disableTimer();

    public abstract void enableTimer(long duration);

    public boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    public static abstract class TimerBarUpdater implements Runnable {

        protected final long duration;
        protected long remainingSeconds;

        public TimerBarUpdater(long duration) {
            this.duration = duration;
            this.remainingSeconds = this.duration;
        }
    }
}
