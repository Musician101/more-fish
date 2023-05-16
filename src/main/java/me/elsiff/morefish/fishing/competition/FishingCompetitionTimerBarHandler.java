package me.elsiff.morefish.fishing.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.Map;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class FishingCompetitionTimerBarHandler {

    private MoreFish getPlugin() {
        return MoreFish.instance();
    }

    private NamespacedKey getTimerBarKey() {
        return new NamespacedKey(getPlugin(), "fishing-competition-timer-bar");
    }
    private FishingCompetitionTimerBarHandler.TimerBarDisplayer barDisplayer;
    private ScheduledTask barUpdatingTask;
    private BossBar timerBar;

    public void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.setTitle(timerBarTitle(0));
        timerBar.setProgress(0D);
        timerBar.removeAll();
        HandlerList.unregisterAll(barDisplayer);
        barDisplayer = null;
        Bukkit.removeBossBar(getTimerBarKey());
        timerBar = null;
    }

    public void enableTimer(long duration) {
        BarColor barColor = BarColor.valueOf(getPlugin().getConfig().getString("messages.contest-bar-color", "blue").toUpperCase());
        timerBar = Bukkit.createBossBar(getTimerBarKey(), "", barColor, BarStyle.SEGMENTED_10);
        Bukkit.getOnlinePlayers().forEach(timerBar::addPlayer);
        barUpdatingTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(getPlugin(), task -> new TimerBarUpdater(duration).run(), 1, 20);
        Bukkit.getPluginManager().registerEvents(barDisplayer = new TimerBarDisplayer(), getPlugin());
    }

    public boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    private String timerBarTitle(long remainingSeconds) {
        return Lang.replace(Lang.TIMER_BOSS_BAR, Map.of("%time%", Lang.time(remainingSeconds)));
    }

    private final class TimerBarDisplayer implements Listener {

        @EventHandler
        public void onPlayerJoin(@Nonnull PlayerJoinEvent event) {
            timerBar.addPlayer(event.getPlayer());
        }

        @EventHandler
        public void onPlayerQuit(@Nonnull PlayerQuitEvent event) {
            timerBar.removePlayer(event.getPlayer());
        }
    }

    private final class TimerBarUpdater implements Runnable {

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
