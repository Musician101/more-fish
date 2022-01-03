package io.musician101.morefish.forge.scheduler;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Scheduler {

    private static final List<Task> TASKS = new ArrayList<>();

    public static void removeAll() {
        TASKS.clear();
    }

    public static void removeTask(Task task) {
        TASKS.remove(task);
    }

    public static void scheduleTask(Task task) {
        if (task.delayLeft == 0) {
            task.run();
            return;
        }

        TASKS.add(task);
    }

    @SubscribeEvent
    public void tick(ServerTickEvent event) {
        for (Task task : TASKS) {
            if (task.tick()) {
                task.run();
                removeTask(task);
            }
        }
    }

}
