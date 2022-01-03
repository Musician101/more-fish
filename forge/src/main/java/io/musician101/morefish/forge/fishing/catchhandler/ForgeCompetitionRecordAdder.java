package io.musician101.morefish.forge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.forge.ForgeMoreFish;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public final class ForgeCompetitionRecordAdder implements ForgeCatchHandler {

    public void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        FishingCompetition<ItemStack> competition = ForgeMoreFish.getInstance().getCompetition();
        if (competition.isEnabled()) {
            competition.putRecord(new Record<>(catcherID.getUniqueID(), fish));
        }

    }
}
