package io.musician101.morefish.forge.fishing.catchhandler;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.config.format.ForgeTextFormat;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractForgeBroadcaster implements ForgeCatchHandler {

    @Nonnull
    public abstract PlayerAnnouncement announcement(@Nonnull Fish var1);

    private ITextComponent fishNameWithRarity(FishType fishType) {
        ITextComponent s = fishType.getDisplayName();
        if (fishType.noDisplay()) {
            return s;
        }

        return fishType.getRarity().getDisplayName().toUpperCase() + " " + s;
    }

    @Nonnull
    protected abstract ForgeTextFormat getCatchMessageFormat();

    public void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        if (meetBroadcastCondition(catcherID, fish)) {
            List<ServerPlayerEntity> receivers = fish.getType().getCatchAnnouncement().receiversOf(catcherID);
            if (ForgeMoreFish.getInstance().getPluginConfig().getMessagesConfig().onlyAnnounceFishingRod()) {
                receivers.removeIf(player -> player.getHeldItem(Hand.MAIN_HAND).getItem() != Items.FISHING_ROD);
            }

            ITextComponent msg = getCatchMessageFormat().replace(ImmutableMap.<String, Object>builder().put("%player%", catcherID.getName()).put("%length%", fish.getLength()).put("%rarity%", fish.getType().getRarity().getDisplayName().toUpperCase()).put("%rarity_color%", fish.getType().getRarity().getColor()).put("%fish%", fish.getType().getName()).put("%fish_with_rarity%", fishNameWithRarity(fish.getType())).build()).output(catcherID);
            receivers.forEach(player -> player.sendMessage(msg, Util.DUMMY_UUID));
        }
    }

    protected abstract boolean meetBroadcastCondition(@Nonnull ServerPlayerEntity player, @Nonnull Fish fish);
}
