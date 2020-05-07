package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;

public final class SpongeNewFirstBroadcaster extends AbstractSpongeBroadcaster {

    @Nonnull
    public SpongePlayerAnnouncement announcement(@Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        return SpongeMoreFish.getInstance().getConfig().getMessagesConfig().getAnnounceNew1st();
    }

    @Nonnull
    public SpongeTextFormat getCatchMessageFormat() {
        return SpongeMoreFish.getInstance().getConfig().getLangConfig().format("get-1st");
    }

    public boolean meetBroadcastCondition(@Nonnull Player catcher, @Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        FishingCompetition<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> competition = SpongeMoreFish.getInstance().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher.getUniqueId(), fish);
    }
}
