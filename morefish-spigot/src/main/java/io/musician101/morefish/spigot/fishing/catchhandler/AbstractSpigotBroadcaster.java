package io.musician101.morefish.spigot.fishing.catchhandler;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractSpigotBroadcaster implements SpigotCatchHandler {

    @Nonnull
    public abstract SpigotPlayerAnnouncement announcement(@Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> var1);

    private String fishNameWithRarity(FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fishType) {
        String s = fishType.getDisplayName();
        if (fishType.noDisplay()) {
            return s;
        }

        return fishType.getRarity().getDisplayName().toUpperCase() + " " + s;
    }

    @Nonnull
    protected abstract SpigotTextFormat getCatchMessageFormat();

    public void handle(@Nonnull Player catcher, @Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        if (meetBroadcastCondition(catcher, fish)) {
            List<Player> receivers = fish.getType().getCatchAnnouncement().receiversOf(catcher);
            if (SpigotMoreFish.getInstance().getPluginConfig().getMessagesConfig().onlyAnnounceFishingRod()) {
                receivers.removeIf(player -> player.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD);
            }

            String msg = getCatchMessageFormat().replace(ImmutableMap.<String, Object>builder().put("%player%", catcher.getName()).put("%length%", fish.getLength()).put("%rarity%", fish.getType().getRarity().getDisplayName().toUpperCase()).put("%rarity_color%", fish.getType().getRarity().getColor()).put("%fish%", fish.getType().getName()).put("%fish_with_rarity%", fishNameWithRarity(fish.getType())).build()).output(catcher);
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }

    protected abstract boolean meetBroadcastCondition(@Nonnull Player player, @Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish);
}
