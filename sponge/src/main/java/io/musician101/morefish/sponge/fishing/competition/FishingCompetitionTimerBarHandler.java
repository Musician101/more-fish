package io.musician101.morefish.sponge.fishing.competition;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ServerSideConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ServerSideConnectionEvent.Join;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;

public final class FishingCompetitionTimerBarHandler {

    private TimerBarDisplayer barDisplayer;
    private ScheduledTask barUpdatingTask;
    private BossBar timerBar;

    public final void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.name(timerBarTitle(0));
        timerBar.progress(0f);
        Sponge.server().onlinePlayers().forEach(player -> player.hideBossBar(timerBar));
        Sponge.eventManager().unregisterListeners(barDisplayer);
        barDisplayer = null;
        timerBar = null;
    }

    public final void enableTimer(long duration) {
        String barColor = SpongeMoreFish.getInstance().getConfig().getMessagesConfig().getContestBarColor();
        timerBar = BossBar.bossBar(Component.text(), 1f, Color.valueOf(barColor.toUpperCase()), Overlay.NOTCHED_10);
        Sponge.server().onlinePlayers().forEach(player -> player.showBossBar(timerBar));
        barUpdatingTask = Sponge.asyncScheduler().submit(Task.builder().delay(Ticks.zero()).interval(1, TimeUnit.SECONDS).execute(new TimerBarUpdater(duration)).plugin(getPlugin()).build());
        Sponge.eventManager().registerListeners(getPlugin(), barDisplayer = new TimerBarDisplayer());
    }

    public final boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    private PluginContainer getPlugin() {
        return SpongeMoreFish.getInstance().getPluginContainer();
    }

    private Component timerBarTitle(long remainingSeconds) {
        LangConfig<SpongeTextFormat, SpongeTextListFormat, Component> langConfig = SpongeMoreFish.getInstance().getConfig().getLangConfig();
        return langConfig.format("timer-boss-bar").replace(ImmutableMap.of("%time%", langConfig.time(remainingSeconds))).output();
    }

    private final class TimerBarDisplayer {

        @Listener
        public final void onPlayerJoin(@Nonnull Join event, @Getter("player") ServerPlayer player) {
            player.showBossBar(timerBar);
        }

        @Listener
        public final void onPlayerQuit(@Nonnull Disconnect event, @Getter("player") ServerPlayer player) {
            player.hideBossBar(timerBar);
        }
    }

    private final class TimerBarUpdater implements Runnable {

        private final float duration;
        private long remainingSeconds;

        public TimerBarUpdater(float duration) {
            this.duration = duration;
            this.remainingSeconds = (long) this.duration;
        }

        @Override
        public void run() {
            remainingSeconds--;
            timerBar.name(timerBarTitle(remainingSeconds));
            timerBar.progress(remainingSeconds / duration);
        }
    }
}
