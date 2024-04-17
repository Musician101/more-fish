package me.elsiff.morefish.announcement;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            case -2 -> PlayerAnnouncement.ofEmpty();
            case -1 -> PlayerAnnouncement.ofServerBroadcast();
            case 0 -> PlayerAnnouncement.ofBaseOnly();
            default -> PlayerAnnouncement.ofRanged(d);
        };
    }

    @NotNull
    static PlayerAnnouncement ofBaseOnly() {
        return new BaseOnlyAnnouncement();
    }

    @NotNull
    static PlayerAnnouncement ofEmpty() {
        return new NoAnnouncement();
    }

    @NotNull
    static PlayerAnnouncement ofRanged(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        return new RangedAnnouncement(radius);
    }

    @NotNull
    static PlayerAnnouncement ofServerBroadcast() {
        return new ServerAnnouncement();
    }

    @NotNull
    List<Player> receiversOf(@NotNull Player var1);
}
