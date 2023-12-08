package me.elsiff.morefish.paper.fishing;

import java.util.List;
import java.util.Random;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.common.fishing.FishRarity;
import me.elsiff.morefish.common.fishing.FishType;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaperFishType extends FishType<PaperFishingCompetition, PaperFish, Item, Player, ItemStack> {

    public PaperFishType(@NotNull String name, @NotNull FishRarity<PaperFishingCompetition, PaperFish, Item, Player> rarity, @NotNull String displayName, double lengthMin, double lengthMax, @NotNull ItemStack icon, @NotNull List<CatchHandler<PaperFish, Player>> catchHandlers, @NotNull PlayerAnnouncement<Player> catchAnnouncement, @NotNull List<FishCondition<PaperFishingCompetition, Item, Player>> fishConditions, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {
        super(name, rarity, displayName, lengthMin, lengthMax, icon, catchHandlers, catchAnnouncement, fishConditions, hasNotFishItemFormat, noDisplay, hasCatchFirework, additionalPrice);
    }

    @NotNull
    public PaperFish generateFish() {
        if (lengthMin > lengthMax) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = lengthMin + new Random().nextDouble() * (lengthMax - lengthMin);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), lengthMin, lengthMax);
        return new PaperFish(this, length);
    }
}
