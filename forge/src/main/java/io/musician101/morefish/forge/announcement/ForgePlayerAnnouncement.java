package io.musician101.morefish.forge.announcement;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import javax.annotation.Nonnull;

public interface ForgePlayerAnnouncement extends PlayerAnnouncement {

    @Nonnull
    static ForgePlayerAnnouncement base() {
        return new ForgeBaseOnlyAnnouncement();
    }

    @Nonnull
    static ForgePlayerAnnouncement empty() {
        return new ForgeNoAnnouncement();
    }

    @Nonnull
    static ForgePlayerAnnouncement ranged(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        return new ForgeRangedAnnouncement(radius);
    }

    @Nonnull
    static ForgePlayerAnnouncement serverBroadcast() {
        return new ForgeServerAnnouncement();
    }
}
