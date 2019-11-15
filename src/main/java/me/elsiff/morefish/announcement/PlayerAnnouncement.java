package me.elsiff.morefish.announcement;

import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public interface PlayerAnnouncement {

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
