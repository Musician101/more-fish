package io.musician101.morefish.spigot.announcement;

import io.musician101.morefish.common.announcement.RangedAnnouncement;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SpigotRangedAnnouncement extends RangedAnnouncement {

    public SpigotRangedAnnouncement(double radius) {
        super(radius);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public List<UUID> receiversOf(@Nonnull UUID catcher) {
        Player p = Bukkit.getPlayer(catcher);
        return p.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(p.getLocation()) <= radius).map(Player::getUniqueId).collect(Collectors.toList());
    }
}
