package io.musician101.morefish.sponge.announcement;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public final class SpongeServerAnnouncement implements PlayerAnnouncement {

    @Nonnull
    public List<UUID> receiversOf(@Nonnull UUID catcher) {
        return Sponge.server().onlinePlayers().stream().map(ServerPlayer::uniqueId).collect(Collectors.toList());
    }
}
