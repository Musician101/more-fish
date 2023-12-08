package me.elsiff.morefish.sponge.fishing.competition;

import java.time.Duration;
import java.util.Map;
import me.elsiff.morefish.common.fishing.competition.TimerBarHandler;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.lang;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public final class SpongeTimerBarHandler extends TimerBarHandler<ScheduledTask> {

    private TimerBarDisplayer barDisplayer;

    @Override
    public void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.name(timerBarTitle(0));
        timerBar.progress(0);
        Sponge.server().onlinePlayers().forEach(player -> player.hideBossBar(timerBar));
        Sponge.eventManager().unregisterListeners(barDisplayer);
        barDisplayer = null;
        Sponge.server().hideBossBar(timerBar);
        timerBar = null;
    }

    @Override
    public void enableTimer(long duration) {
        Color barColor = Color.NAMES.valueOr(getPlugin().getConfig().node("messages.contest-bar-color").getString("blue"), Color.BLUE);
        timerBar = BossBar.bossBar(timerBarTitle(duration), 100, barColor, Overlay.NOTCHED_10);
        Sponge.server().onlinePlayers().forEach(player -> player.hideBossBar(timerBar));
        barUpdatingTask = Sponge.asyncScheduler().submit(Task.builder().execute(new SpongeTimerBarUpdater(duration)).delay(Ticks.zero()).delay(Duration.ofSeconds(1)).build());
        Sponge.eventManager().registerListeners(getPlugin().getPluginContainer(), barDisplayer = new TimerBarDisplayer());
    }

    private Component timerBarTitle(long remainingSeconds) {
        return lang().replace(join(text("Fishing Contest ", Style.style(AQUA, BOLD)), text("[%time% left]")), Map.of("%time%", lang().time(remainingSeconds)));
    }

    private final class SpongeTimerBarUpdater extends TimerBarUpdater {

        public SpongeTimerBarUpdater(long duration) {
            super(duration);
        }

        public void run() {
            remainingSeconds--;
            timerBar.name(timerBarTitle(remainingSeconds));
            timerBar.progress((float) remainingSeconds / duration);
        }
    }

    private final class TimerBarDisplayer {

        @Listener
        public void onPlayerJoin(@NotNull ServerSideConnectionEvent.Join event) {
            event.player().showBossBar(timerBar);
        }

        @Listener
        public void onPlayerQuit(@NotNull ServerSideConnectionEvent.Disconnect event) {
            event.player().hideBossBar(timerBar);
        }
    }
}
