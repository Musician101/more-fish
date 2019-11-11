package me.elsiff.morefish.announcement;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public final class BaseOnlyAnnouncement implements PlayerAnnouncement {

    @Nonnull
    public List<Player> receiversOf(@Nonnull Player catcher) {
        return Collections.singletonList(catcher);
    }
}
