package me.elsiff.morefish.configuration.loader;

import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import org.bukkit.configuration.ConfigurationSection;

public final class PlayerAnnouncementLoader implements CustomLoader<PlayerAnnouncement> {

    @Nonnull
    public PlayerAnnouncement loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        double configuredValue = section.getDouble(path);
        switch ((int) configuredValue) {
            case -2:
                return PlayerAnnouncement.ofEmpty();
            case -1:
                return PlayerAnnouncement.ofServerBroadcast();
            case 0:
                return PlayerAnnouncement.ofBaseOnly();
            default:
                return PlayerAnnouncement.ofRanged(configuredValue);
        }
    }
}
