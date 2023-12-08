package me.elsiff.morefish.announcement;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ServerAnnouncement implements PlayerAnnouncement {

    @NotNull
    public List<Player> receiversOf(@NotNull Player catcher) {
        return new ArrayList<>(catcher.getServer().getOnlinePlayers());
    }
}
