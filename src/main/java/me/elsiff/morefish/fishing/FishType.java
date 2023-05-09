package me.elsiff.morefish.fishing;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.condition.FishCondition;
import org.bukkit.inventory.ItemStack;

public record FishType(@Nonnull String name, @Nonnull FishRarity rarity, @Nonnull String displayName, double lengthMin,
                       double lengthMax, @Nonnull ItemStack icon, @Nonnull List<CatchHandler> catchHandlers,
                       @Nonnull PlayerAnnouncement catchAnnouncement, @Nonnull List<FishCondition> conditions,
                       boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework,
                       double additionalPrice) {

    private double clamp(double value, double min, double max) {
        double var7 = Math.min(value, max);
        return Math.max(var7, min);
    }

    private double floorToTwoDecimalPlaces(double value) {
        double var3 = value * (double) 10;
        return Math.floor(var3) / (double) 10;
    }

    @Nonnull
    public Fish generateFish() {
        if (lengthMin > lengthMax) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = lengthMin + new Random().nextDouble() * (lengthMax - lengthMin);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), lengthMin, lengthMax);
        return new Fish(this, length);
    }
}
