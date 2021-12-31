package me.elsiff.morefish.fishing.competition;

import java.util.Map;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionTimerBarHandler {

    private final MoreFish plugin = MoreFish.instance();
    private final NamespacedKey timerBarKey = new NamespacedKey(plugin, "fishing-competition-timer-bar");
    private FishingCompetitionTimerBarHandler.TimerBarDisplayer barDisplayer;
    private BukkitTask barUpdatingTask;
    private BossBar timerBar;

    public void disableTimer() {
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

    public void enableTimer(long duration) {
        BarColor barColor = BarColor.valueOf(plugin.getConfig().getString("messages.contest-bar-color", "blue").toUpperCase());
        timerBar = plugin.getServer().createBossBar(timerBarKey, "", barColor, BarStyle.SEGMENTED_10);
        plugin.getServer().getOnlinePlayers().forEach(timerBar::addPlayer);
        barUpdatingTask = new TimerBarUpdater(duration).runTaskTimer(plugin, 0, 20L);
        plugin.getServer().getPluginManager().registerEvents(barDisplayer = new TimerBarDisplayer(), plugin);
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
