package me.elsiff.morefish.configuration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.competition.Prize;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public interface Config {

    private static FileConfiguration getConfig() {
        return MoreFish.instance().getConfig();
    }

    @Nonnull
    static PlayerAnnouncement getDefaultCatchAnnouncement() {
        ConfigurationSection cs = getConfig().getConfigurationSection("messages");
        double configuredValue = -1;
        if (cs != null) {
            configuredValue = cs.getDouble("announce-catch", -1);
        }

        return PlayerAnnouncement.fromValue(configuredValue);
    }

    @Nonnull
    static Map<Integer, Prize> getPrizes() {
        ConfigurationSection cs = getConfig().getConfigurationSection("contest-prizes");
        if (cs == null) {
            return Map.of();
        }

        return cs.getKeys(false).stream().collect(Collectors.toMap(key -> Integer.parseInt(key) - 1, key -> new Prize(cs.getStringList(key))));
    }

    @Nonnull
    static List<LocalTime> getScheduledTimes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return getConfig().getStringList("auto-running.start-time").stream().map(string -> LocalTime.parse(string, formatter)).toList();
    }
}
