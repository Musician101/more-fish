package me.elsiff.morefish.announcement;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class NoAnnouncement implements PlayerAnnouncement {

    @NotNull
    public List<Player> receiversOf(@NotNull Player catcher) {
        return List.of();
    }
}
