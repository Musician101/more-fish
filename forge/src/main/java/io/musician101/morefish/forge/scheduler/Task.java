package io.musician101.morefish.forge.scheduler;

public abstract class Task implements Runnable {

    final int delay;
    int delayLeft;

    public Task(int delay) {
        this.delay = delay;
        this.delayLeft = delay;
    }

    public void cancel() {
        Scheduler.removeTask(this);
    }

    public boolean tick() {
        this.delayLeft--;
        return this.delayLeft <= 0;
    }
}
