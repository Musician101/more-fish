package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SpigotNewFirstBroadcaster extends AbstractSpigotBroadcaster {

    @Nonnull
    public SpigotPlayerAnnouncement announcement(@Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        return SpigotMoreFish.getInstance().getPluginConfig().getMessagesConfig().getAnnounceNew1st();
    }

    @Nonnull
    public SpigotTextFormat getCatchMessageFormat() {
        return SpigotMoreFish.getInstance().getPluginConfig().getLangConfig().format("get-1st");
    }

    public boolean meetBroadcastCondition(@Nonnull Player catcher, @Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        FishingCompetition<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> competition = SpigotMoreFish.getInstance().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher.getUniqueId(), fish);
    }
}
