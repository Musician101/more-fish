package io.musician101.morefish.spigot.announcement;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public interface SpigotPlayerAnnouncement extends PlayerAnnouncement<Player> {

    @Nonnull
    static SpigotPlayerAnnouncement base() {
        return new SpigotBaseOnlyAnnouncement();
    }

    @Nonnull
    static SpigotPlayerAnnouncement empty() {
        return new SpigotNoAnnouncement();
    }

    @Nonnull
    static SpigotPlayerAnnouncement ranged(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        return new SpigotRangedAnnouncement(radius);
    }

    @Nonnull
    static SpigotPlayerAnnouncement serverBroadcast() {
        return new SpigotServerAnnouncement();
    }
}
