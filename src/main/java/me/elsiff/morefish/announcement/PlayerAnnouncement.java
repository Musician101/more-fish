package me.elsiff.morefish.announcement;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface PlayerAnnouncement {

    @Nonnull
    static PlayerAnnouncement fromConfigOrDefault(@Nullable ConfigurationSection cs, @Nonnull String path, @Nonnull PlayerAnnouncement def) {
        if (cs == null || !cs.contains(path)) {
            return def;
        }

        return fromValue(cs.getDouble(path));
    }

    @Nonnull
    static PlayerAnnouncement fromValue(double d) {
        return switch ((int) d) {
            case -2 -> PlayerAnnouncement.ofEmpty();
            case -1 -> PlayerAnnouncement.ofServerBroadcast();
            case 0 -> PlayerAnnouncement.ofBaseOnly();
            default -> PlayerAnnouncement.ofRanged(d);
        };
    }

    @Nonnull
    static PlayerAnnouncement ofBaseOnly() {
        return new BaseOnlyAnnouncement();
    }

    @Nonnull
    static PlayerAnnouncement ofEmpty() {
        return new NoAnnouncement();
    }

    @Nonnull
    static PlayerAnnouncement ofRanged(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        return new RangedAnnouncement(radius);
    }

    @Nonnull
    static PlayerAnnouncement ofServerBroadcast() {
        return new ServerAnnouncement();
    }

    @Nonnull
    List<Player> receiversOf(@Nonnull Player var1);
}
