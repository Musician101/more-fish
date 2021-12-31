package me.elsiff.morefish.announcement;

import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public final class NoAnnouncement implements PlayerAnnouncement {

    @Nonnull
    public List<Player> receiversOf(@Nonnull Player catcher) {
        return List.of();
    }
}
