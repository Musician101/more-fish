package io.musician101.morefish.sponge.announcement;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;

public interface SpongePlayerAnnouncement extends PlayerAnnouncement<Player> {

    @Nonnull
    static SpongePlayerAnnouncement base() {
        return new SpongeBaseOnlyAnnouncement();
    }

    @Nonnull
    static SpongePlayerAnnouncement empty() {
        return new SpongeNoAnnouncement();
    }

    @Nonnull
    static SpongePlayerAnnouncement ranged(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        return new SpongeRangedAnnouncement(radius);
    }

    @Nonnull
    static SpongePlayerAnnouncement serverBroadcast() {
        return new SpongeServerAnnouncement();
    }
}
