package me.elsiff.morefish.fishing.competition;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.PREFIX_STRING;
import static me.elsiff.morefish.text.Lang.replace;

public final class FishingCompetitionAutoRunner {

    private static final long HALF_MINUTE = 600L;
    private BukkitTask timeCheckingTask;

    public void disable() {
        if (timeCheckingTask == null) {
            throw new IllegalStateException("Auto runner must not be disabled");
        }

        timeCheckingTask.cancel();
        timeCheckingTask = null;
    }

    @SuppressWarnings("DataFlowIssue")
    public void enable() {
        if (timeCheckingTask != null) {
            throw new IllegalStateException("Auto runner must not be already enabled");
        }

        ConfigurationSection config = getPlugin().getConfig().getConfigurationSection("auto-running");
        int requiredPlayers = config.getInt("required-players");
        long duration = config.getLong("timer") * 20L;
        List<CompetitionTimes> competitionTimes = getCompetitionTimes();
        List<Duration> displayedReminders = new ArrayList<>();
        timeCheckingTask = new BukkitRunnable() {

            @Override
            public void run() {
                LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
                if (competitionTimes.stream().anyMatch(time -> time.matchesReminderTimes(currentTime) && !displayedReminders.contains(time.reminderTimes.get(currentTime)))) {
                    StringBuilder sb = new StringBuilder(PREFIX_STRING).append("<white>If there are <gold>").append(requiredPlayers).append(" <white>or more players online in <gold>");
                    competitionTimes.stream().filter(c -> c.matchesReminderTimes(currentTime)).findFirst().ifPresent(c -> {
                        Duration remainingTime = c.reminderTimes.get(currentTime);
                        int hours = remainingTime.toHoursPart();
                        int minutes = remainingTime.toMinutesPart();
                        displayedReminders.add(remainingTime);
                        if (hours > 0) {
                            sb.append(hours).append(" hour").append((hours > 1 ? "s" : ""));
                        }

                        if (minutes > 0) {
                            if (hours > 0) {
                                sb.append(" ");
                            }

                            sb.append(minutes).append(" minute").append((minutes > 1 ? "s" : ""));
                        }
                    });
                    sb.append("<white>, the next competition will begin!");
                    Bukkit.broadcast(replace(sb.toString()));
                }
                else if (Bukkit.getOnlinePlayers().size() >= requiredPlayers) {
                    if (competitionTimes.stream().anyMatch(time -> time.matchesStartTime(currentTime)) && getCompetitionHost().getCompetition().isDisabled()) {
                        getCompetitionHost().openCompetitionFor(duration);
                        displayedReminders.clear();
                    }
                }
            }
        }.runTaskTimer(getPlugin(), 0, HALF_MINUTE);
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

        public boolean matchesStartTime(@NotNull LocalTime time) {
            return startTime.equals(time);
        }

        public boolean matchesReminderTimes(@NotNull LocalTime time) {
            return reminderTimes.containsKey(time);
        }
    }
}
