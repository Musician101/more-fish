package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;

public final class SpongeCompetitionRecordAdder implements SpongeCatchHandler {

    public void handle(@Nonnull Player catcher, @Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        FishingCompetition<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> competition = SpongeMoreFish.getInstance().getCompetition();
        if (competition.isEnabled()) {
            competition.putRecord(new Record<>(catcher.getUniqueId(), fish));
        }

    }
}
