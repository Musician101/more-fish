package io.musician101.morefish.common.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;

public final class AutoRunningConfig implements ConfigModule {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    protected boolean enabled = false;
    protected int requiredPlayers = 5;
    protected List<LocalTime> startTimes = Stream.of("09:00", "11:30", "14:00", "18:00", "21:00").map(this::parseLocalTime).collect(Collectors.toList());
    protected int timer = 300;

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

    @Override
    public void load(@Nonnull ConfigurationNode node) {
        if (node.isVirtual()) {
            return;
        }

        enabled = node.getNode("enable").getBoolean(false);
        requiredPlayers = node.getNode("required-player").getInt(5);
        timer = node.getNode("timer").getInt(300);
        startTimes = node.getNode("start-time").getList(Object::toString).stream().map(this::parseLocalTime).collect(Collectors.toList());
    }

    protected final LocalTime parseLocalTime(String s) {
        return LocalTime.parse(s, FORMATTER);
    }
}
