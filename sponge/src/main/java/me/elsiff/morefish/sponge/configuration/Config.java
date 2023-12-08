package me.elsiff.morefish.sponge.configuration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.sponge.announcement.SpongePlayerAnnouncement;
import me.elsiff.morefish.sponge.fishing.competition.SpongePrize;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public interface Config {

    private static ConfigurationNode getConfig() {
        return getPlugin().getConfig();
    }

    @NotNull
    static PlayerAnnouncement<ServerPlayer> getDefaultCatchAnnouncement() {
        ConfigurationNode cn = getConfig().node("messages");
        double configuredValue = -1;
        if (!cn.virtual()) {
            configuredValue = cn.node("announce-catch").getDouble(-1);
        }

        return SpongePlayerAnnouncement.fromValue(configuredValue);
    }

    @NotNull
    static Map<Integer, SpongePrize> getPrizes() {
        ConfigurationNode cn = getConfig().node("contest-prizes");
        if (cn.virtual()) {
            return Map.of();
        }

        return cn.childrenMap().entrySet().stream().collect(Collectors.toMap(e -> Integer.parseInt(e.getKey().toString()) - 1, e -> {
            try {
                return new SpongePrize(e.getValue().getList(String.class, List.of()));
            }
            catch (SerializationException ex) {
                getPlugin().getLogger().error("Error reading " + e.getValue().path(), ex);
                return new SpongePrize(List.of("/tell @p You have received a broken reward. Please contact server staff."));
            }
        }));
    }

    @NotNull
    static List<LocalTime> getScheduledTimes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            return getConfig().node("auto-running.start-time").getList(String.class, List.of()).stream().map(string -> LocalTime.parse(string, formatter)).toList();
        }
        catch (SerializationException e) {
            getPlugin().getLogger().error("Error reading auto-running.start-time", e);
            return List.of();
        }
    }
}
