package me.elsiff.morefish.competition;

import com.google.common.collect.Lists;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishingCompetitionAutoRunner {

    @Nullable
    private ScheduledTask timeCheckingTask;

    public void disable() {
        if (timeCheckingTask == null) {
            throw new IllegalStateException("Auto runner must not be disabled");
        }

        timeCheckingTask.cancel();
        timeCheckingTask = null;
    }

    private void checkTime(CompetitionTimes c, LocalTime currentTime, int requiredPlayers) {
        StringBuilder sb = new StringBuilder();
        Duration remainingTime = c.reminderTimes.get(currentTime);
        int hours = remainingTime.toHoursPart();
        int minutes = remainingTime.toMinutesPart();
        if (hours > 0) {
            sb.append(hours);
            if (hours > 1) {
                sb.append(" <lang:morefish.main.announcement.pre-announcement.hour.plural>");
            }
            else {
                sb.append(" <lang:morefish.main.announcement.pre-announcement.hour.singular>");
            }
        }

        if (minutes > 0) {
            if (hours > 0) {
                sb.append(" ");
            }

            sb.append(minutes);
            if (minutes > 1) {
                sb.append(" <lang:morefish.main.announcement.pre-announcement.minute.plural>");
            }
            else {
                sb.append(" <lang:morefish.main.announcement.pre-announcement.minute.singular>");
            }
        }

        ComponentLike requiredPlayersArgument = Argument.numeric("required-players", requiredPlayers);
        ComponentLike timeRemainingArgument = Argument.tagResolver(Placeholder.parsed("time-remaining", sb.toString()));
        Bukkit.broadcast(Component.translatable("morefish.main.announcement.pre-announcement.message", requiredPlayersArgument, timeRemainingArgument));
    }

    public void enable() {
        if (timeCheckingTask != null) {
            throw new IllegalStateException("Auto runner must not be already enabled");
        }

        timeCheckingTask = Bukkit.getAsyncScheduler().runAtFixedRate(getPlugin(), task -> {
            FileConfiguration config = getPlugin().getConfig();
            int requiredPlayers = config.getInt("auto-running.required-players", 5);
            long duration = config.getLong("auto-running.timer", 300) * 20L;
            List<CompetitionTimes> competitionTimes = getCompetitionTimes();
            LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
            if (competitionTimes.stream().anyMatch(time -> time.matchesReminderTimes(currentTime))) {
                competitionTimes.stream().filter(c -> c.matchesReminderTimes(currentTime)).findFirst().ifPresent(c -> checkTime(c, currentTime, requiredPlayers));
            }
            else if (Bukkit.getOnlinePlayers().size() >= requiredPlayers) {
                if (competitionTimes.stream().anyMatch(time -> time.matchesStartTime(currentTime)) && !getCompetitionHost().getCompetition().isEnabled()) {
                    getCompetitionHost().openCompetitionFor(duration);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public List<CompetitionTimes> getCompetitionTimes() {
        List<String> times = getPlugin().getConfig().getStringList("auto-running.start-time");
        return Lists.transform(times, CompetitionTimes::new);
    }

    private FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    public boolean isEnabled() {
        return this.timeCheckingTask != null;
    }

    public static class CompetitionTimes {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        private final LocalTime startTime;
        private final Map<LocalTime, Duration> reminderTimes = new HashMap<>();

        CompetitionTimes(String startTime) {
            this.startTime = LocalTime.parse(startTime, FORMATTER);
            getPlugin().getConfig().getStringList("auto-running.reminder-timings").stream().map(s -> {
                String[] args = s.split(":");
                return Duration.ofHours(Integer.parseInt(args[0])).plusMinutes(Long.parseLong(args[1]));
            }).forEach(d -> reminderTimes.put(this.startTime.minus(d), d));
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public boolean matchesStartTime(LocalTime time) {
            return startTime.equals(time);
        }

        public boolean matchesReminderTimes(LocalTime time) {
            return reminderTimes.containsKey(time);
        }
    }
}
