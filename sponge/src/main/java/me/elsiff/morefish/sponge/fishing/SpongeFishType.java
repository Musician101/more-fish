package me.elsiff.morefish.sponge.fishing;

import java.util.List;
import java.util.Random;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.common.fishing.FishRarity;
import me.elsiff.morefish.common.fishing.FishType;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeFishType extends FishType<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer, ItemStack> {

    public SpongeFishType(@NotNull String name, @NotNull FishRarity<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer> rarity, @NotNull String displayName, double lengthMin, double lengthMax, @NotNull ItemStack icon, @NotNull List<CatchHandler<SpongeFish, ServerPlayer>> catchHandlers, @NotNull PlayerAnnouncement<ServerPlayer> catchAnnouncement, @NotNull List<FishCondition<SpongeFishingCompetition, Item, ServerPlayer>> fishConditions, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {
        super(name, rarity, displayName, lengthMin, lengthMax, icon, catchHandlers, catchAnnouncement, fishConditions, hasNotFishItemFormat, noDisplay, hasCatchFirework, additionalPrice);
    }

    @NotNull
    public SpongeFish generateFish() {
        if (lengthMin > lengthMax) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = lengthMin + new Random().nextDouble() * (lengthMax - lengthMin);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), lengthMin, lengthMax);
        return new SpongeFish(this, length);
    }
}
