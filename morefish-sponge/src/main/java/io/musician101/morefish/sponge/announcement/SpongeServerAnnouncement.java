package io.musician101.morefish.sponge.announcement;

import io.musician101.morefish.common.announcement.ServerAnnouncement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongeServerAnnouncement extends ServerAnnouncement<Player> implements SpongePlayerAnnouncement {

    @Nonnull
    public List<Player> receiversOf(@Nonnull Player catcher) {
        return new ArrayList<>(Sponge.getServer().getOnlinePlayers());
    }
}
