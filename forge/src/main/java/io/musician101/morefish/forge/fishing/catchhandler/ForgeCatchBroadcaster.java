package io.musician101.morefish.forge.fishing.catchhandler;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.announcement.ForgeNoAnnouncement;
import io.musician101.morefish.forge.config.format.ForgeTextFormat;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;

public final class ForgeCatchBroadcaster extends AbstractForgeBroadcaster {

    @Nonnull
    public PlayerAnnouncement announcement(@Nonnull Fish fish) {
        return fish.getType().getCatchAnnouncement();
    }

    @Nonnull
    public ForgeTextFormat getCatchMessageFormat() {
        return ForgeMoreFish.getInstance().getPluginConfig().getLangConfig().format("catch-fish");
    }

    public boolean meetBroadcastCondition(@Nonnull ServerPlayerEntity catcher, @Nonnull Fish fish) {
        return !(fish.getType().getCatchAnnouncement() instanceof ForgeNoAnnouncement);
    }
}
