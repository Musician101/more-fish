package io.musician101.morefish.spigot.announcement;

import io.musician101.morefish.common.announcement.RangedAnnouncement;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public final class SpigotRangedAnnouncement extends RangedAnnouncement<Player> implements SpigotPlayerAnnouncement {

    public SpigotRangedAnnouncement(double radius) {
        super(radius);
    }

    @Nonnull
    @Override
    public List<Player> receiversOf(@Nonnull Player catcher) {
        return catcher.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(catcher.getLocation()) <= radius).collect(Collectors.toList());
    }
}
