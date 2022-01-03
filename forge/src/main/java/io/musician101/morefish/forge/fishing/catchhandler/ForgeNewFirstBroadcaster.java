package io.musician101.morefish.forge.fishing.catchhandler;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.config.format.ForgeTextFormat;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public final class ForgeNewFirstBroadcaster extends AbstractForgeBroadcaster {

    @Nonnull
    public PlayerAnnouncement announcement(@Nonnull Fish fish) {
        return ForgeMoreFish.getInstance().getPluginConfig().getMessagesConfig().getAnnounceNew1st();
    }

    @Nonnull
    public ForgeTextFormat getCatchMessageFormat() {
        return ForgeMoreFish.getInstance().getPluginConfig().getLangConfig().format("get-1st");
    }

    public boolean meetBroadcastCondition(@Nonnull ServerPlayerEntity catcher, @Nonnull Fish fish) {
        FishingCompetition<ItemStack> competition = ForgeMoreFish.getInstance().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher.getUniqueID(), fish);
    }
}
