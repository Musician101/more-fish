package me.elsiff.morefish.announcement;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface PlayerAnnouncement {

    @NotNull
    static PlayerAnnouncement fromConfigOrDefault(@Nullable JsonObject json, @NotNull String path, @NotNull PlayerAnnouncement def) {
        if (json == null || !json.has(path)) {
            return def;
        }

        return fromValue(json.getAsJsonPrimitive(path).getAsDouble());
    }

    @NotNull
    static PlayerAnnouncement fromValue(double d) {
        return switch ((int) d) {
            case -2 -> PlayerAnnouncement.empty();
            case -1 -> PlayerAnnouncement.serverBroadcast();
            case 0 -> PlayerAnnouncement.catcherOnly();
            default -> PlayerAnnouncement.ranged(d);
        };
    }

    @NotNull
    static PlayerAnnouncement catcherOnly() {
        return List::of;
    }

    @NotNull
    static PlayerAnnouncement empty() {
        return catcher -> List.of();
    }

    @NotNull
    static PlayerAnnouncement ranged(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        return catcher -> catcher.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(catcher.getLocation()) <= radius).toList();
    }

    @NotNull
    static PlayerAnnouncement serverBroadcast() {
        return catcher -> new ArrayList<>(catcher.getServer().getOnlinePlayers());
    }

    @NotNull
    List<Player> receiversOf(@NotNull Player catcher);
}
