package me.elsiff.morefish.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.elsiff.morefish.lang.ArgumentUtil;
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishingCompetitionTimerBarHandler implements Listener {

    @Nullable
    private ScheduledTask barUpdatingTask;
    @Nullable
    private BossBar timerBar;
    private long remainingSeconds;

    public void disableTimer() {
        if (barUpdatingTask != null) {
            barUpdatingTask.cancel();
            barUpdatingTask = null;
        }

        if (timerBar != null) {
            timerBar.name(timerBarTitle(0));
            timerBar.progress(0);
            Bukkit.getOnlinePlayers().forEach(player -> player.hideBossBar(timerBar));
        }

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
            if (remainingSeconds == 0) {
                return;
            }

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
        return new NamespacedKey(getPlugin(), "competition-timer-bar");
    }

    private Component timerBarTitle(long remainingSeconds) {
        return Component.translatable("morefish.main.timer-bar-title", ArgumentUtil.timeRemaining(remainingSeconds));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (timerBar != null) {
            event.getPlayer().showBossBar(timerBar);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (timerBar != null) {
            event.getPlayer().hideBossBar(timerBar);
        }
    }
}
