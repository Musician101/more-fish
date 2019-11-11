package me.elsiff.morefish.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class OneTickScheduler {

    private final Plugin plugin;
    private final Multimap<Object, OneTickRunnable> runnableMap = HashMultimap.create();

    public OneTickScheduler(@Nonnull Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    public final void cancelAllOf(@Nonnull Object client) {
        runnableMap.get(client).stream().filter(runnable -> !runnable.isCancelled()).forEach(BukkitRunnable::cancel);
        runnableMap.removeAll(client);
    }

    public final void scheduleLater(@Nonnull Object client, @Nonnull Runnable action) {
        OneTickRunnable runnable = new OneTickRunnable(client, action);
        runnable.runTaskLater(plugin, 1);
        runnableMap.put(client, runnable);
    }

    private final class OneTickRunnable extends BukkitRunnable {

        private final Runnable action;
        private final Object client;

        public OneTickRunnable(@Nonnull Object client, @Nonnull Runnable action) {
            super();
            this.client = client;
            this.action = action;
        }

        public void run() {
            this.action.run();
            runnableMap.remove(client, this);
        }
    }
}
