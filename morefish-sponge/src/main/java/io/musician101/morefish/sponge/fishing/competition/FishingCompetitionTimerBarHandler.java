package io.musician101.morefish.sponge.fishing.competition;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

public final class FishingCompetitionTimerBarHandler {

    private TimerBarDisplayer barDisplayer;
    private Task barUpdatingTask;
    private ServerBossBar timerBar;

    public final void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.setName(timerBarTitle(0));
        timerBar.setPercent(0f);
        timerBar.removePlayers(timerBar.getPlayers());
        Sponge.getEventManager().unregisterListeners(barDisplayer);
        barDisplayer = null;
        timerBar = null;
    }

    public final void enableTimer(long duration) {
        BossBarColor barColor = SpongeMoreFish.getInstance().getConfig().getMessagesConfig().getContestBarColor();
        timerBar = ServerBossBar.builder().visible(true).color(barColor).createFog(false).playEndBossMusic(false).darkenSky(false).overlay(BossBarOverlays.NOTCHED_10).name(Text.EMPTY).build();
        timerBar.addPlayers(Sponge.getServer().getOnlinePlayers());
        barUpdatingTask = Task.builder().delayTicks(0).interval(1, TimeUnit.SECONDS).execute(new TimerBarUpdater(duration)).submit(getPlugin());
        Sponge.getEventManager().registerListeners(getPlugin(), barDisplayer = new TimerBarDisplayer());
    }

    public final boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    private SpongeMoreFish getPlugin() {
        return SpongeMoreFish.getInstance();
    }

    private Text timerBarTitle(long remainingSeconds) {
        LangConfig<SpongeTextFormat, SpongeTextListFormat, Text> langConfig = SpongeMoreFish.getInstance().getConfig().getLangConfig();
        return langConfig.format("timer-boss-bar").replace(ImmutableMap.of("%time%", langConfig.time(remainingSeconds))).output();
    }

    private final class TimerBarDisplayer {

        @Listener
        public final void onPlayerJoin(@Nonnull Join event) {
            timerBar.addPlayer(event.getTargetEntity());
        }

        @Listener
        public final void onPlayerQuit(@Nonnull Disconnect event) {
            timerBar.removePlayer(event.getTargetEntity());
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
            timerBar.setName(timerBarTitle(remainingSeconds));
            timerBar.setPercent(remainingSeconds / duration);
        }
    }
}
