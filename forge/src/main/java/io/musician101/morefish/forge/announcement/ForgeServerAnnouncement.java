package io.musician101.morefish.forge.announcement;

import io.musician101.morefish.common.announcement.ServerAnnouncement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class ForgeServerAnnouncement extends ServerAnnouncement implements ForgePlayerAnnouncement {

    public ForgeServerAnnouncement() {
        super(allPlayersSupplier);
    }

    @Nonnull
    public @Nonnull
    List<UUID> receiversOf(@Nonnull UUID catcher) {
        return new ArrayList<>(catcher.getServer().getPlayerList().getPlayers());
    }
}
