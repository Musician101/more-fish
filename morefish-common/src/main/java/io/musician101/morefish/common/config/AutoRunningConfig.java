package io.musician101.morefish.common.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.Types;

public final class AutoRunningConfig implements ConfigModule {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    protected boolean enabled = false;
    protected int requiredPlayers = 5;
    protected List<LocalTime> startTimes = Stream.of("09:00", "11:30", "14:00", "18:00", "21:00").map(s -> LocalTime.parse(s, FORMATTER)).collect(Collectors.toList());
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void load(@Nonnull ConfigurationNode node) {
        if (node.isVirtual()) {
            return;
        }

        enabled = node.getNode("enable").getBoolean(false);
        requiredPlayers = node.getNode("required-player").getInt(5);
        timer = node.getNode("timer").getInt(300);
        startTimes = node.getNode("start-time").getList(o -> LocalTime.parse(Types.asString(o), FORMATTER));
    }
}
