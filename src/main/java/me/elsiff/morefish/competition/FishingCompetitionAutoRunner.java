package me.elsiff.morefish.competition;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.elsiff.morefish.text.Lang;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingCompetitionAutoRunner {

    private ScheduledTask timeCheckingTask;

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

        FileConfiguration config = getPlugin().getConfig();
        int requiredPlayers = config.getInt("auto-running.required-players", 5);
        long duration = config.getLong("auto-running.timer", 300) * 20L;
        List<CompetitionTimes> competitionTimes = getCompetitionTimes();
        timeCheckingTask = Bukkit.getAsyncScheduler().runAtFixedRate(getPlugin(), task -> {
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
            if (competitionTimes.stream().anyMatch(time -> time.matchesReminderTimes(currentTime))) {
                competitionTimes.stream().filter(c -> c.matchesReminderTimes(currentTime)).findFirst().ifPresent(c -> {
                    StringBuilder sb = new StringBuilder();
                    Duration remainingTime = c.reminderTimes.get(currentTime);
                    int hours = remainingTime.toHoursPart();
                    int minutes = remainingTime.toMinutesPart();
                    if (hours > 0) {
                        sb.append(hours).append(" hour").append((hours > 1 ? "s" : ""));
                    }

                    if (minutes > 0) {
                        if (hours > 0) {
                            sb.append(" ");
                        }

                        sb.append(minutes).append(" minute").append((minutes > 1 ? "s" : ""));
                    }

                    Bukkit.broadcast(Lang.replace("<mf-lang:pre-announcement>", TagResolver.resolver(Lang.tagResolver("time-remaining", sb.toString()), Formatter.number("required-players", requiredPlayers))));
                });
            }
            else if (Bukkit.getOnlinePlayers().size() >= requiredPlayers) {
                if (competitionTimes.stream().anyMatch(time -> time.matchesStartTime(currentTime)) && !getCompetitionHost().getCompetition().isEnabled()) {
                    getCompetitionHost().openCompetitionFor(duration);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    @NotNull
    public List<CompetitionTimes> getCompetitionTimes() {
        return getPlugin().getConfig().getStringList("auto-running.start-time").stream().map(CompetitionTimes::new).toList();
    }

    private FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    public boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public static class CompetitionTimes {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        @NotNull
        private final LocalTime startTime;
        @NotNull
        private final Map<LocalTime, Duration> reminderTimes = new HashMap<>();

        CompetitionTimes(@NotNull String startTime) {
            this.startTime = LocalTime.parse(startTime, FORMATTER);
            getPlugin().getConfig().getStringList("auto-running.reminder-timings").stream().map(s -> {
                String[] args = s.split(":");
                return Duration.ofHours(Integer.parseInt(args[0])).plusMinutes(Long.parseLong(args[1]));
            }).forEach(d -> reminderTimes.put(this.startTime.minus(d), d));
        }

        public @NotNull LocalTime getStartTime() {
            return startTime;
        }

        public boolean matchesStartTime(@NotNull LocalTime time) {
            return startTime.equals(time);
        }

        public boolean matchesReminderTimes(@NotNull LocalTime time) {
            return reminderTimes.containsKey(time);
        }
    }
}
