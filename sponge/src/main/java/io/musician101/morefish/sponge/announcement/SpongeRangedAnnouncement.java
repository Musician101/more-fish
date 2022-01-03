package io.musician101.morefish.sponge.announcement;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.announcement.RangedAnnouncement;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public final class SpongeRangedAnnouncement extends RangedAnnouncement implements PlayerAnnouncement {

    public SpongeRangedAnnouncement(double radius) {
        super(radius);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Nonnull
    @Override
    public List<UUID> receiversOf(@Nonnull UUID catcher) {
        ServerPlayer p = Sponge.server().player(catcher).get();
        return p.world().players().stream().filter(player -> player.position().distance(p.position()) <= radius).map(ServerPlayer::uniqueId).collect(Collectors.toList());
    }
}
