package me.elsiff.morefish.fishing;

import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.condition.FishCondition;
import org.bukkit.inventory.ItemStack;

public final class FishType {

    private final double additionalPrice;
    @Nonnull
    private final PlayerAnnouncement catchAnnouncement;
    @Nonnull
    private final List<CatchHandler> catchHandlers;
    @Nonnull
    private final Set<FishCondition> conditions;
    @Nonnull
    private final String displayName;
    private final boolean hasCatchFirework;
    private final boolean hasNotFishItemFormat;
    @Nonnull
    private final ItemStack icon;
    private final double lengthMax;
    private final double lengthMin;
    @Nonnull
    private final String name;
    private final boolean noDisplay;
    @Nonnull
    private final FishRarity rarity;

    public FishType(@Nonnull String name, @Nonnull FishRarity rarity, @Nonnull String displayName, double lengthMin, double lengthMax, @Nonnull ItemStack icon, @Nonnull List catchHandlers, @Nonnull PlayerAnnouncement catchAnnouncement, @Nonnull Set conditions, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {
        this.name = name;
        this.rarity = rarity;
        this.displayName = displayName;
        this.lengthMin = lengthMin;
        this.lengthMax = lengthMax;
        this.icon = icon;
        this.catchHandlers = catchHandlers;
        this.catchAnnouncement = catchAnnouncement;
        this.conditions = conditions;
        this.hasNotFishItemFormat = hasNotFishItemFormat;
        this.noDisplay = noDisplay;
        this.hasCatchFirework = hasCatchFirework;
        this.additionalPrice = additionalPrice;
    }

    private final double clamp(double value, double min, double max) {
        double var7 = Math.min(value, max);
        return Math.max(var7, min);
    }

    private final double floorToTwoDecimalPlaces(double value) {
        double var3 = value * (double) 10;
        return Math.floor(var3) / (double) 10;
    }

    @Nonnull
    public final Fish generateFish() {
        if (lengthMin > lengthMax) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = lengthMin + new Random().nextDouble() * (lengthMax - lengthMin);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), lengthMin, lengthMax);
        return new Fish(this, length);
    }

    public final double getAdditionalPrice() {
        return this.additionalPrice;
    }

    @Nonnull
    public final PlayerAnnouncement getCatchAnnouncement() {
        return this.catchAnnouncement;
    }

    @Nonnull
    public final List<CatchHandler> getCatchHandlers() {
        return this.catchHandlers;
    }

    @Nonnull
    public final Set<FishCondition> getConditions() {
        return this.conditions;
    }

    @Nonnull
    public final String getDisplayName() {
        return this.displayName;
    }

    public final boolean getHasCatchFirework() {
        return this.hasCatchFirework;
    }

    public final boolean getHasNotFishItemFormat() {
        return this.hasNotFishItemFormat;
    }

    @Nonnull
    public final ItemStack getIcon() {
        return this.icon;
    }

    public final double getLengthMax() {
        return this.lengthMax;
    }

    public final double getLengthMin() {
        return this.lengthMin;
    }

    @Nonnull
    public final String getName() {
        return this.name;
    }

    public final boolean getNoDisplay() {
        return this.noDisplay;
    }

    @Nonnull
    public final FishRarity getRarity() {
        return this.rarity;
    }
}
