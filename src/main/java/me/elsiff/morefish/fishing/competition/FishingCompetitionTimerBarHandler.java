package me.elsiff.morefish.fishing.competition;

import com.google.common.collect.ImmutableMap;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionTimerBarHandler {

    private final Plugin plugin;
    private FishingCompetitionTimerBarHandler.TimerBarDisplayer barDisplayer;
    private BukkitTask barUpdatingTask;
    private BossBar timerBar;
    private NamespacedKey timerBarKey;

    public FishingCompetitionTimerBarHandler(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.timerBarKey = new NamespacedKey(this.plugin, "fishing-competition-timer-bar");
    }

    public final void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.setTitle(timerBarTitle(0));
        timerBar.setProgress(0D);
        timerBar.removeAll();
        HandlerList.unregisterAll(barDisplayer);
        barDisplayer = null;
        plugin.getServer().removeBossBar(timerBarKey);
        timerBar = null;
    }

    public final void enableTimer(long duration) {
        BarColor barColor = BarColor.valueOf(Config.INSTANCE.getStandard().getString("messages.contest-bar-color").toUpperCase());
        timerBar = plugin.getServer().createBossBar(timerBarKey, "", barColor, BarStyle.SEGMENTED_10);
        plugin.getServer().getOnlinePlayers().forEach(timerBar::addPlayer);
        barUpdatingTask = new TimerBarUpdater(duration).runTaskTimer(plugin, 0, 20L);
        barDisplayer = new TimerBarDisplayer();
        plugin.getServer().getPluginManager().registerEvents(barDisplayer, plugin);
    }

    public final boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    private final String timerBarTitle(long remainingSeconds) {
        return Lang.INSTANCE.format("timer-boss-bar").replace(ImmutableMap.of("%time%", Lang.INSTANCE.time(remainingSeconds))).output();
    }

    private final class TimerBarDisplayer implements Listener {

        public TimerBarDisplayer() {
        }

        @EventHandler
        public final void onPlayerJoin(@Nonnull PlayerJoinEvent event) {
            timerBar.removePlayer(event.getPlayer());
        }

        @EventHandler
        public final void onPlayerQuit(@Nonnull PlayerQuitEvent event) {
            timerBar.addPlayer(event.getPlayer());
        }
    }

    private final class TimerBarUpdater extends BukkitRunnable {

        private final long duration;
        private long remainingSeconds;

        public TimerBarUpdater(long duration) {
            this.duration = duration;
            this.remainingSeconds = this.duration;
        }

        public void run() {
            remainingSeconds--;
            timerBar.setTitle(timerBarTitle(remainingSeconds));
            timerBar.setProgress((double) remainingSeconds / duration);
        }
    }
}
