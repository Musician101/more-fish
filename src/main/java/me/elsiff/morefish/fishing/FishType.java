package me.elsiff.morefish.fishing;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.condition.FishCondition;
import org.bukkit.inventory.ItemStack;

public record FishType(@Nonnull String name, @Nonnull FishRarity rarity, @Nonnull String displayName, double lengthMin, double lengthMax, @Nonnull ItemStack icon, @Nonnull List<CatchHandler> catchHandlers, @Nonnull PlayerAnnouncement catchAnnouncement, @Nonnull List<FishCondition> conditions, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {

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

    public double getAdditionalPrice() {
        return this.additionalPrice;
    }

    @Nonnull
    public PlayerAnnouncement getCatchAnnouncement() {
        return this.catchAnnouncement;
    }

    @Nonnull
    public List<CatchHandler> getCatchHandlers() {
        return this.catchHandlers;
    }

    @Nonnull
    public List<FishCondition> getConditions() {
        return this.conditions;
    }

    @Nonnull
    public String getDisplayName() {
        return this.displayName;
    }

    public boolean getHasCatchFirework() {
        return this.hasCatchFirework;
    }

    public boolean getHasNotFishItemFormat() {
        return this.hasNotFishItemFormat;
    }

    @Nonnull
    public ItemStack getIcon() {
        return this.icon;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public boolean getNoDisplay() {
        return this.noDisplay;
    }

    @Nonnull
    public FishRarity getRarity() {
        return this.rarity;
    }
}
