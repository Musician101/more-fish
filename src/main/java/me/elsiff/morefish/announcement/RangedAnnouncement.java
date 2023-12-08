package me.elsiff.morefish.announcement;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record RangedAnnouncement(double radius) implements PlayerAnnouncement {

    @NotNull
    public List<Player> receiversOf(@NotNull Player catcher) {
        return catcher.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(catcher.getLocation()) <= radius).collect(Collectors.toList());
    }
}
