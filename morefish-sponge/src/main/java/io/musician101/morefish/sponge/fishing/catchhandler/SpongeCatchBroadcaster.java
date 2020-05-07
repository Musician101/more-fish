package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongeNoAnnouncement;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;

public final class SpongeCatchBroadcaster extends AbstractSpongeBroadcaster {

    @Nonnull
    public SpongePlayerAnnouncement announcement(@Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        return fish.getType().getCatchAnnouncement();
    }

    @Nonnull
    public SpongeTextFormat getCatchMessageFormat() {
        return SpongeMoreFish.getInstance().getConfig().getLangConfig().format("catch-fish");
    }

    public boolean meetBroadcastCondition(@Nonnull Player catcher, @Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        return !(fish.getType().getCatchAnnouncement() instanceof SpongeNoAnnouncement);
    }
}
