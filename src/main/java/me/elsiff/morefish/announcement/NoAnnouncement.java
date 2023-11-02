package me.elsiff.morefish.announcement;

import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class NoAnnouncement implements PlayerAnnouncement {

    @NotNull
    public List<Player> receiversOf(@NotNull Player catcher) {
        return List.of();
    }
}
