package me.elsiff.morefish.fishing.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.Map;
import me.elsiff.morefish.configuration.Lang;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;


public final class FishingCompetitionTimerBarHandler {

    private FishingCompetitionTimerBarHandler.TimerBarDisplayer barDisplayer;
    private ScheduledTask barUpdatingTask;
    private BossBar timerBar;

    public void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.name(timerBarTitle(0));
        timerBar.progress(0);
        Bukkit.getOnlinePlayers().forEach(player -> player.hideBossBar(timerBar));
        HandlerList.unregisterAll(barDisplayer);
        barDisplayer = null;
        Bukkit.removeBossBar(getTimerBarKey());
        timerBar = null;
    }

    public void enableTimer(long duration) {
        barUpdatingTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(getPlugin(), task -> new TimerBarUpdater(duration).run(), 1, 20);
        Color barColor = Color.NAMES.valueOr(getPlugin().getConfig().getString("messages.contest-bar-color", "blue"), Color.BLUE);
        timerBar = BossBar.bossBar(timerBarTitle(duration), 100, barColor, Overlay.NOTCHED_10);
        Bukkit.getOnlinePlayers().forEach(player -> player.showBossBar(timerBar));
        Bukkit.getPluginManager().registerEvents(barDisplayer = new TimerBarDisplayer(), getPlugin());
    }

    public boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    private NamespacedKey getTimerBarKey() {
        return new NamespacedKey(getPlugin(), "fishing-competition-timer-bar");
    }

    private Component timerBarTitle(long remainingSeconds) {
        return Lang.replace(join(text("Fishing Contest ", Style.style(AQUA, BOLD)), text("[%time% left]")), Map.of("%time%", Lang.time(remainingSeconds)));
    }

    private final class TimerBarDisplayer implements Listener {

        @EventHandler
        public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
            event.getPlayer().showBossBar(timerBar);
        }

        @EventHandler
        public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
            event.getPlayer().showBossBar(timerBar);
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
            timerBar.name(timerBarTitle(remainingSeconds));
            timerBar.progress((float) remainingSeconds / duration);
        }
    }
}
