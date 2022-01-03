package io.musician101.morefish.spigot.announcement;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SpigotServerAnnouncement implements PlayerAnnouncement {

    @Nonnull
    public List<UUID> receiversOf(@Nonnull UUID catcher) {
        return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList());
    }
}
