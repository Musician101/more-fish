package me.elsiff.morefish.configuration.loader;

import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import org.bukkit.configuration.ConfigurationSection;

public final class PlayerAnnouncementLoader implements CustomLoader<PlayerAnnouncement> {

    @Nonnull
    public PlayerAnnouncement loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        double configuredValue = section.getDouble(path);
        PlayerAnnouncement announcement;
        switch ((int) configuredValue) {
            case -2:
                announcement = PlayerAnnouncement.ofEmpty();
                break;
            case -1:
                announcement = PlayerAnnouncement.ofServerBroadcast();
                break;
            case 0:
                announcement = PlayerAnnouncement.ofBaseOnly();
                break;
            default:
                announcement = PlayerAnnouncement.ofRanged(configuredValue);
        }

        return announcement;
    }
}
