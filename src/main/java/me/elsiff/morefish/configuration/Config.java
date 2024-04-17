package me.elsiff.morefish.configuration;

import me.elsiff.morefish.fishing.competition.Prize;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

public interface Config {

    private static FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    @NotNull
    static Map<Integer, Prize> getPrizes() {
        ConfigurationSection cs = getConfig().getConfigurationSection("contest-prizes");
        if (cs == null) {
            return Map.of();
        }

        return cs.getKeys(false).stream().collect(Collectors.toMap(key -> Integer.parseInt(key) - 1, key -> new Prize(cs.getStringList(key))));
    }

    @NotNull
    static List<LocalTime> getScheduledTimes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return getConfig().getStringList("auto-running.start-time").stream().map(string -> LocalTime.parse(string, formatter)).toList();
    }
}
