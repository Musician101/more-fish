package me.elsiff.morefish.fishing.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.replace;
import static me.elsiff.morefish.text.Lang.timeRemaining;

public final class FishingCompetitionTimerBarHandler implements Listener {

    private ScheduledTask barUpdatingTask;
    private BossBar timerBar;
    private long remainingSeconds;

    public void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.name(timerBarTitle(0));
        timerBar.progress(0);
        Bukkit.getOnlinePlayers().forEach(player -> player.hideBossBar(timerBar));
        HandlerList.unregisterAll(this);
        Bukkit.removeBossBar(getTimerBarKey());
        timerBar = null;
    }

    public void enableTimer(long duration) {
        this.remainingSeconds = duration;
        Color barColor = Color.NAMES.valueOr(getPlugin().getConfig().getString("messages.contest-bar-color", "blue"), Color.BLUE);
        timerBar = BossBar.bossBar(timerBarTitle(duration), 1, barColor, Overlay.NOTCHED_10);
        Bukkit.getOnlinePlayers().forEach(player -> player.showBossBar(timerBar));
        barUpdatingTask = Bukkit.getAsyncScheduler().runAtFixedRate(getPlugin(), task -> {
            remainingSeconds--;
            timerBar.name(timerBarTitle(remainingSeconds));
            timerBar.progress((float) remainingSeconds / duration);
        }, 0, 1, TimeUnit.SECONDS);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    public boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    private NamespacedKey getTimerBarKey() {
        return new NamespacedKey(getPlugin(), "morefish-timer-bar");
    }

    private Component timerBarTitle(long remainingSeconds) {
        return replace("<mf-lang:timer-bar-title>", timeRemaining(remainingSeconds));
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        event.getPlayer().showBossBar(timerBar);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        event.getPlayer().hideBossBar(timerBar);
    }
}
