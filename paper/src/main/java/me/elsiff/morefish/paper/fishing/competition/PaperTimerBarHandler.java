package me.elsiff.morefish.paper.fishing.competition;

import java.util.Map;
import me.elsiff.morefish.common.fishing.competition.TimerBarHandler;
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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;
import static me.elsiff.morefish.paper.configuration.PaperLang.join;
import static me.elsiff.morefish.paper.configuration.PaperLang.lang;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public final class PaperTimerBarHandler extends TimerBarHandler<BukkitTask> {

    private TimerBarDisplayer barDisplayer;

    @Override
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

    @Override
    public void enableTimer(long duration) {
        Color barColor = Color.NAMES.valueOr(getPlugin().getConfig().getString("messages.contest-bar-color", "blue"), Color.BLUE);
        timerBar = BossBar.bossBar(timerBarTitle(duration), 100, barColor, Overlay.NOTCHED_10);
        Bukkit.getOnlinePlayers().forEach(player -> player.hideBossBar(timerBar));
        barUpdatingTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), new PaperTimerBarUpdater(duration), 0, 20L);
        Bukkit.getPluginManager().registerEvents(barDisplayer = new TimerBarDisplayer(), getPlugin());
    }

    private NamespacedKey getTimerBarKey() {
        return new NamespacedKey(getPlugin(), "fishing-competition-timer-bar");
    }

    private Component timerBarTitle(long remainingSeconds) {
        return lang().replace(join(text("Fishing Contest ", Style.style(AQUA, BOLD)), text("[%time% left]")), Map.of("%time%", lang().time(remainingSeconds)));
    }

    private final class PaperTimerBarUpdater extends TimerBarUpdater {

        public PaperTimerBarUpdater(long duration) {
            super(duration);
        }

        public void run() {
            remainingSeconds--;
            timerBar.name(timerBarTitle(remainingSeconds));
            timerBar.progress((float) remainingSeconds / duration);
        }
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
}
