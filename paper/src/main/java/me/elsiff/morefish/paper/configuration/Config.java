package me.elsiff.morefish.paper.configuration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.paper.announcement.PaperPlayerAnnouncement;
import me.elsiff.morefish.paper.fishing.competition.PaperPrize;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public interface Config {

    private static FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    @NotNull
    static PlayerAnnouncement<Player> getDefaultCatchAnnouncement() {
        ConfigurationSection cs = getConfig().getConfigurationSection("messages");
        double configuredValue = -1;
        if (cs != null) {
            configuredValue = cs.getDouble("announce-catch", -1);
        }

        return PaperPlayerAnnouncement.fromValue(configuredValue);
    }

    @NotNull
    static Map<Integer, PaperPrize> getPrizes() {
        ConfigurationSection cs = getConfig().getConfigurationSection("contest-prizes");
        if (cs == null) {
            return Map.of();
        }

        return cs.getKeys(false).stream().collect(Collectors.toMap(key -> Integer.parseInt(key) - 1, key -> new PaperPrize(cs.getStringList(key))));
    }

    @NotNull
    static List<LocalTime> getScheduledTimes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return getConfig().getStringList("auto-running.start-time").stream().map(string -> LocalTime.parse(string, formatter)).toList();
    }
}
