package io.musician101.morefish.sponge.announcement;

import io.musician101.morefish.common.announcement.RangedAnnouncement;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongeRangedAnnouncement extends RangedAnnouncement<Player> implements SpongePlayerAnnouncement {

    public SpongeRangedAnnouncement(double radius) {
        super(radius);
    }

    @Nonnull
    @Override
    public List<Player> receiversOf(@Nonnull Player catcher) {
        return catcher.getWorld().getPlayers().stream().filter(player -> player.getLocation().getPosition().distance(catcher.getLocation().getPosition()) <= radius).collect(Collectors.toList());
    }
}
