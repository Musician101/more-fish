package me.elsiff.morefish.announcement;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public final class RangedAnnouncement implements PlayerAnnouncement {

    private final double radius;

    public RangedAnnouncement(double radius) {
        this.radius = radius;
    }

    @Nonnull
    public List<Player> receiversOf(@Nonnull Player catcher) {
        return catcher.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(catcher.getLocation()) <= radius).collect(Collectors.toList());
    }
}
