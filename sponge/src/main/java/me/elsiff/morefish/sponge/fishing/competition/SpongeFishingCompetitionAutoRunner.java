package me.elsiff.morefish.sponge.fishing.competition;

import java.time.LocalTime;
import me.elsiff.morefish.common.fishing.competition.FishingCompetitionAutoRunner;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.configurate.ConfigurationNode;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public final class SpongeFishingCompetitionAutoRunner extends FishingCompetitionAutoRunner<ScheduledTask> {

    public void disable() {
        if (timeCheckingTask == null) {
            throw new IllegalStateException("Auto runner must not be disabled");
        }

        timeCheckingTask.cancel();
        timeCheckingTask = null;
    }

    public void enable() {
        if (timeCheckingTask != null) {
            throw new IllegalStateException("Auto runner must not be already enabled");
        }

        timeCheckingTask = Sponge.asyncScheduler().submit(Task.builder().execute(() -> {
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
            scheduledTimes.stream().filter(time -> time.equals(currentTime)).forEach(time -> tryOpenCompetition());
        }).delay(Ticks.zero()).interval(Ticks.of(HALF_MINUTE)).plugin(getPlugin().getPluginContainer()).build());
    }

    private SpongeFishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    @Override
    protected void tryOpenCompetition() {
        ConfigurationNode config = getPlugin().getConfig();
        int requiredPlayers = config.node("auto-running.required-players").getInt();
        if (getCompetitionHost().getCompetition().isDisabled() && Sponge.server().onlinePlayers().size() >= requiredPlayers) {
            long duration = config.node("auto-running.timer").getLong() * 20L;
            getCompetitionHost().openCompetitionFor(duration);
        }

    }
}
