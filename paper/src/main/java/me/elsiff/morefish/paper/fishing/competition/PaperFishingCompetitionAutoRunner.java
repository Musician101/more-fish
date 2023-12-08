package me.elsiff.morefish.paper.fishing.competition;

import java.time.LocalTime;
import me.elsiff.morefish.common.fishing.competition.FishingCompetitionAutoRunner;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public final class PaperFishingCompetitionAutoRunner extends FishingCompetitionAutoRunner<BukkitTask> {

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

        timeCheckingTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
            scheduledTimes.stream().filter(time -> time.equals(currentTime)).forEach(time -> tryOpenCompetition());
        }, 0, HALF_MINUTE);
    }

    private PaperFishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    @Override
    protected void tryOpenCompetition() {
        FileConfiguration config = getPlugin().getConfig();
        int requiredPlayers = config.getInt("auto-running.required-players");
        if (getCompetitionHost().getCompetition().isDisabled() && Bukkit.getOnlinePlayers().size() >= requiredPlayers) {
            long duration = config.getLong("auto-running.timer") * 20L;
            getCompetitionHost().openCompetitionFor(duration);
        }

    }
}
