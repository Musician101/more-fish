package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.announcement.NoAnnouncement;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SpongeCatchBroadcaster extends AbstractSpongeBroadcaster {

    @Nonnull
    public SpongeTextFormat getCatchMessageFormat() {
        return SpongeMoreFish.getInstance().getConfig().getLangConfig().format("catch-fish");
    }

    @Override
    protected boolean meetBroadcastCondition(@Nonnull UUID player, @Nonnull Fish fish) {
        return !(fish.getType().getCatchAnnouncement() instanceof NoAnnouncement);
    }
}
