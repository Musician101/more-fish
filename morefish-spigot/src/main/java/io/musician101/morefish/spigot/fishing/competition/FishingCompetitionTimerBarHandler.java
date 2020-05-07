package io.musician101.morefish.spigot.fishing.competition;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import javax.annotation.Nonnull;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionTimerBarHandler {

    private final NamespacedKey timerBarKey = new NamespacedKey(getPlugin(), "fishing-competition-timer-bar");
    private TimerBarDisplayer barDisplayer;
    private BukkitTask barUpdatingTask;
    private BossBar timerBar;

    public final void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.setTitle(timerBarTitle(0));
        timerBar.setProgress(0D);
        timerBar.removeAll();
        HandlerList.unregisterAll(barDisplayer);
        barDisplayer = null;
        Bukkit.removeBossBar(timerBarKey);
        timerBar = null;
    }

    public final void enableTimer(long duration) {
        BarColor barColor = SpigotMoreFish.getInstance().getPluginConfig().getMessagesConfig().getContestBarColor();
        timerBar = Bukkit.createBossBar(timerBarKey, "", barColor, BarStyle.SEGMENTED_10);
        Bukkit.getOnlinePlayers().forEach(timerBar::addPlayer);
        barUpdatingTask = new TimerBarUpdater(duration).runTaskTimer(getPlugin(), 0, 20L);
        Bukkit.getPluginManager().registerEvents(barDisplayer = new TimerBarDisplayer(), getPlugin());
    }

    public final boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    private SpigotMoreFish getPlugin() {
        return SpigotMoreFish.getInstance();
    }

    private String timerBarTitle(long remainingSeconds) {
        LangConfig<SpigotTextFormat, SpigotTextListFormat, String> langConfig = SpigotMoreFish.getInstance().getPluginConfig().getLangConfig();
        return langConfig.format("timer-boss-bar").replace(ImmutableMap.of("%time%", langConfig.time(remainingSeconds))).output();
    }

    private final class TimerBarDisplayer implements Listener {

        @EventHandler
        public final void onPlayerJoin(@Nonnull PlayerJoinEvent event) {
            timerBar.addPlayer(event.getPlayer());
        }

        @EventHandler
        public final void onPlayerQuit(@Nonnull PlayerQuitEvent event) {
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
