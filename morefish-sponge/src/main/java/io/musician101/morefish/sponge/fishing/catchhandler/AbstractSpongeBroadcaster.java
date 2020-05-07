package io.musician101.morefish.sponge.fishing.catchhandler;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.List;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

public abstract class AbstractSpongeBroadcaster implements SpongeCatchHandler {

    @Nonnull
    public abstract SpongePlayerAnnouncement announcement(@Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> var1);

    private String fishNameWithRarity(FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fishType) {
        String s = fishType.getDisplayName();
        if (fishType.noDisplay()) {
            return s;
        }

        return fishType.getRarity().getDisplayName().toUpperCase() + " " + s;
    }

    @Nonnull
    protected abstract SpongeTextFormat getCatchMessageFormat();

    public void handle(@Nonnull Player catcher, @Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        if (meetBroadcastCondition(catcher, fish)) {
            List<Player> receivers = fish.getType().getCatchAnnouncement().receiversOf(catcher);
            if (SpongeMoreFish.getInstance().getConfig().getMessagesConfig().onlyAnnounceFishingRod()) {
                receivers.removeIf(player -> player.getItemInHand(HandTypes.MAIN_HAND).filter(itemStack -> itemStack.getType() != ItemTypes.FISHING_ROD).isPresent());
            }

            Text msg = getCatchMessageFormat().replace(ImmutableMap.<String, Object>builder().put("%player%", catcher.getName()).put("%length%", fish.getLength()).put("%rarity%", fish.getType().getRarity().getDisplayName().toUpperCase()).put("%rarity_color%", fish.getType().getRarity().getColor()).put("%fish%", fish.getType().getName()).put("%fish_with_rarity%", fishNameWithRarity(fish.getType())).build()).output(catcher);
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }

    protected abstract boolean meetBroadcastCondition(@Nonnull Player var1, @Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> var2);
}
