package me.elsiff.morefish.common.fishing.competition;

import org.jetbrains.annotations.NotNull;

public abstract class FishingCompetitionHost<B extends TimerBarHandler<T>, C, T> {

    @NotNull protected final B timerBarHandler;
    protected T timerTask;

    protected FishingCompetitionHost(@NotNull B timerBarHandler) {
        this.timerBarHandler = timerBarHandler;
    }

    public void closeCompetition() {
        closeCompetition(false);
    }

    public abstract void closeCompetition(boolean suspend);

    public abstract void informAboutRanking(@NotNull C receiver);

    public abstract void openCompetition();

    public abstract void openCompetitionFor(long tick);
}
