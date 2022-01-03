package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.announcement.NoAnnouncement;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SpigotCatchBroadcaster extends AbstractSpigotBroadcaster {

    @Nonnull
    @Override
    public SpigotTextFormat getCatchMessageFormat() {
        return SpigotMoreFish.getInstance().getPluginConfig().getLangConfig().format("catch-fish");
    }

    @Override
    protected boolean meetBroadcastCondition(@Nonnull UUID catcher, @Nonnull Fish fish) {
        return !(fish.getType().getCatchAnnouncement() instanceof NoAnnouncement);
    }
}
