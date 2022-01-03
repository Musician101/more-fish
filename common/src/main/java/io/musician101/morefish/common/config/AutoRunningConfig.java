package io.musician101.morefish.common.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public final class AutoRunningConfig {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private boolean enabled = false;
    private int requiredPlayers = 5;
    private List<LocalTime> startTimes = Stream.of("09:00", "11:30", "14:00", "18:00", "21:00").map(s -> LocalTime.parse(s, FORMATTER)).collect(Collectors.toList());
    private int timer = 300;

    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    public List<LocalTime> getStartTimes() {
        return startTimes;
    }

    public int getTimer() {
        return timer;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void load(@Nonnull ConfigurationNode node) throws SerializationException {
        if (node.empty()) {
            return;
        }

        enabled = node.node("enable").getBoolean(false);
        requiredPlayers = node.node("required-player").getInt(5);
        timer = node.node("timer").getInt(300);
        startTimes = node.node("start-time").getList(String.class, new ArrayList<>()).stream().map(o -> LocalTime.parse(o, FORMATTER)).collect(Collectors.toList());
    }
}
