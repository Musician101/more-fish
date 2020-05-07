package io.musician101.morefish.spigot.announcement;

import io.musician101.morefish.common.announcement.ServerAnnouncement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public final class SpigotServerAnnouncement extends ServerAnnouncement<Player> implements SpigotPlayerAnnouncement {

    @Nonnull
    public List<Player> receiversOf(@Nonnull Player catcher) {
        return new ArrayList<>(catcher.getServer().getOnlinePlayers());
    }
}
