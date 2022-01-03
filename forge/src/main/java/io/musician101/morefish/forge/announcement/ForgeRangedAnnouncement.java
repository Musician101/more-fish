package io.musician101.morefish.forge.announcement;

import io.musician101.morefish.common.announcement.RangedAnnouncement;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;

public final class ForgeRangedAnnouncement extends RangedAnnouncement implements ForgePlayerAnnouncement {

    public ForgeRangedAnnouncement(double radius) {
        super(radius);
    }

    @Nonnull
    @Override
    public @Nonnull
    List<UUID> receiversOf(@Nonnull UUID catcher) {
        return catcher.getEntityWorld().getPlayers().stream().filter(player -> player.getPositionVec().distanceTo(catcher.getPositionVec()) <= radius).map(ServerPlayerEntity.class::cast).collect(Collectors.toList());
    }
}
