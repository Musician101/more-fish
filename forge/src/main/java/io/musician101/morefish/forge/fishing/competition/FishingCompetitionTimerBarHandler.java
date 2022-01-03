package io.musician101.morefish.forge.fishing.competition;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.format.TextFormat;
import io.musician101.morefish.common.config.format.TextListFormat;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.scheduler.Task;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public final class FishingCompetitionTimerBarHandler {

    private TimerBarDisplayer barDisplayer;
    private Task barUpdatingTask;
    private ServerBossInfo timerBar;

    public final void disableTimer() {
        barUpdatingTask.cancel();
        barUpdatingTask = null;
        timerBar.setName(timerBarTitle(0));
        timerBar.setPercent(0);
        timerBar.removeAllPlayers();
        MinecraftForge.EVENT_BUS.unregister(barDisplayer);
        barDisplayer = null;
        timerBar = null;
    }

    public final void enableTimer(long duration) {
        Object barColor = ForgeMoreFish.getInstance().getPluginConfig().getMessagesConfig().getContestBarColor();
        timerBar = new ServerBossInfo(new StringTextComponent(""), barColor, Overlay.NOTCHED_10);
        LogicalSidedProvider.INSTANCE.<MinecraftServer>get(LogicalSide.SERVER).getPlayerList().getPlayers().forEach(timerBar::addPlayer);
        barUpdatingTask = new TimerBarUpdater(duration);
        MinecraftForge.EVENT_BUS.register(barDisplayer = new TimerBarDisplayer());
    }

    public final boolean getHasTimerEnabled() {
        return this.timerBar != null;
    }

    private ForgeMoreFish getPlugin() {
        return ForgeMoreFish.getInstance();
    }

    private ITextComponent timerBarTitle(long remainingSeconds) {
        LangConfig<TextFormat<?, ?>, TextListFormat<?, ?, ?>, Object> langConfig = ForgeMoreFish.getInstance().getPluginConfig().getLangConfig();
        return langConfig.format("timer-boss-bar").replace(ImmutableMap.of("%time%", langConfig.time(remainingSeconds))).output();
    }

    private final class TimerBarDisplayer {

        @SubscribeEvent
        public final void onPlayerJoin(@Nonnull PlayerLoggedInEvent event) {
            timerBar.addPlayer((ServerPlayerEntity) event.getPlayer());
        }

        @SubscribeEvent
        public final void onPlayerQuit(@Nonnull PlayerLoggedOutEvent event) {
            timerBar.removePlayer((ServerPlayerEntity) event.getPlayer());
        }
    }

    private final class TimerBarUpdater extends Task {

        private final long duration;
        private long remainingSeconds;

        public TimerBarUpdater(long duration) {
            super(0);
            this.duration = duration;
            this.remainingSeconds = this.duration;
        }

        public void run() {
            remainingSeconds--;
            timerBar.setName(timerBarTitle(remainingSeconds));
            timerBar.setPercent((float) remainingSeconds / duration);
        }
    }
}
