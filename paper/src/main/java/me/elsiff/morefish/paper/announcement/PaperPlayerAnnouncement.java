package me.elsiff.morefish.paper.announcement;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PaperPlayerAnnouncement extends PlayerAnnouncement<Player> {

    @NotNull
    static PlayerAnnouncement<Player> fromConfigOrDefault(@Nullable JsonObject json, @NotNull String path, @NotNull PlayerAnnouncement<Player> def) {
        if (json == null || !json.has(path)) {
            return def;
        }

        return fromValue(json.getAsJsonPrimitive(path).getAsDouble());
    }

    @NotNull
    static PlayerAnnouncement<Player> fromValue(double d) {
        return switch ((int) d) {
            case -2 -> PlayerAnnouncement.ofEmpty();
            case -1 -> PaperPlayerAnnouncement.ofServerBroadcast();
            case 0 -> PlayerAnnouncement.ofBaseOnly();
            default -> PaperPlayerAnnouncement.ofRanged(d);
        };
    }

    @NotNull
    static PlayerAnnouncement<Player> ofRanged(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        return new PaperRangedAnnouncement(radius);
    }

    @NotNull
    static PlayerAnnouncement<Player> ofServerBroadcast() {
        return new PaperServerAnnouncement();
    }

    final class PaperRangedAnnouncement extends RangedAnnouncement<Player> {

        public PaperRangedAnnouncement(double radius) {
            super(radius);
        }

        @NotNull
        public List<Player> receiversOf(@NotNull Player catcher) {
            return catcher.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(catcher.getLocation()) <= radius).collect(Collectors.toList());
        }
    }

    final class PaperServerAnnouncement implements PlayerAnnouncement<Player> {

        @NotNull
        public List<Player> receiversOf(@NotNull Player catcher) {
            return new ArrayList<>(catcher.getServer().getOnlinePlayers());
        }
    }
}
