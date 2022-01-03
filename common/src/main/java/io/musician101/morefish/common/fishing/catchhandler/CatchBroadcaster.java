package io.musician101.morefish.common.fishing.catchhandler;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.format.TextFormat;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public abstract class CatchBroadcaster<F extends TextFormat<F, R>, R> implements CatchHandler {

    private static String fishNameWithRarity(FishType fishType) {
        String s = fishType.getDisplayName();
        if (fishType.noDisplay()) {
            return s;
        }

        return fishType.getRarity().getDisplayName().toUpperCase() + " " + s;
    }

    private static Map<String, Object> getReplaceObjects(String name, Fish fish) {
        return ImmutableMap.<String, Object>builder().put("%player%", name).put("%length%", fish.getLength()).put("%rarity%", fish.getType().getRarity().getDisplayName().toUpperCase()).put("%rarity_color%", fish.getType().getRarity().getColor()).put("%fish%", fish.getType().getName()).put("%fish_with_rarity%", fishNameWithRarity(fish.getType())).build();
    }

    @Nonnull
    protected abstract F getCatchMessageFormat();

    @Nonnull
    protected abstract String getCatcherName(@Nonnull UUID uuid);

    @Override
    public final void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        if (meetBroadcastCondition(catcherID, fish)) {
            List<UUID> receivers = fish.getType().getCatchAnnouncement().receiversOf(catcherID);
            if (onlyAnnounceFishingRod()) {
                receivers.removeIf(this::hasFishingRod);
            }

            R msg = getCatchMessageFormat().replace(getReplaceObjects(getCatcherName(catcherID), fish)).output(catcherID);
            receivers.forEach(uuid -> sendMessage(uuid, msg));
        }
    }

    protected abstract boolean hasFishingRod(@Nonnull UUID uuid);

    protected abstract boolean meetBroadcastCondition(@Nonnull UUID player, @Nonnull Fish fish);

    protected abstract boolean onlyAnnounceFishingRod();

    protected abstract void sendMessage(@Nonnull UUID uuid, @Nonnull R msg);
}
