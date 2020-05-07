package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotNoAnnouncement;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SpigotCatchBroadcaster extends AbstractSpigotBroadcaster {

    @Nonnull
    public SpigotPlayerAnnouncement announcement(@Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        return fish.getType().getCatchAnnouncement();
    }

    @Nonnull
    public SpigotTextFormat getCatchMessageFormat() {
        return SpigotMoreFish.getInstance().getPluginConfig().getLangConfig().format("catch-fish");
    }

    public boolean meetBroadcastCondition(@Nonnull Player catcher, @Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        return !(fish.getType().getCatchAnnouncement() instanceof SpigotNoAnnouncement);
    }
}
