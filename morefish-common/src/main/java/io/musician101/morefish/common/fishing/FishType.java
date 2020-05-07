package io.musician101.morefish.common.fishing;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

public final class FishType<A extends PlayerAnnouncement<?>, B, C extends FishCondition<?, ?>, H extends CatchHandler<?, ?>, I> {

    private final double additionalPrice;
    @Nonnull
    private final A catchAnnouncement;
    @Nonnull
    private final List<H> catchHandlers;
    @Nonnull
    private final List<C> conditions;
    @Nonnull
    private final String displayName;
    private final boolean hasCatchFirework;
    private final boolean hasNotFishItemFormat;
    @Nonnull
    private final I icon;
    private final double lengthMax;
    private final double lengthMin;
    @Nonnull
    private final String name;
    private final boolean noDisplay;
    @Nonnull
    private final FishRarity<A, B, H> rarity;

    public FishType(@Nonnull String name, @Nonnull FishRarity<A, B, H> rarity, @Nonnull String displayName, double lengthMin, double lengthMax, @Nonnull I icon, @Nonnull List<H> catchHandlers, @Nonnull A catchAnnouncement, @Nonnull List<C> conditions, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {
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

    private double clamp(double value, double min, double max) {
        double var7 = Math.min(value, max);
        return Math.max(var7, min);
    }

    private double floorToTwoDecimalPlaces(double value) {
        double var3 = value * (double) 10;
        return Math.floor(var3) / (double) 10;
    }

    @Nonnull
    public Fish<A, B, C, H, I> generateFish() {
        if (lengthMin > lengthMax) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = lengthMin + new Random().nextDouble() * (lengthMax - lengthMin);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), lengthMin, lengthMax);
        return new Fish<>(this, length);
    }

    public final double getAdditionalPrice() {
        return this.additionalPrice;
    }

    @Nonnull
    public A getCatchAnnouncement() {
        return this.catchAnnouncement;
    }

    @Nonnull
    public List<H> getCatchHandlers() {
        return this.catchHandlers;
    }

    @Nonnull
    public List<C> getConditions() {
        return this.conditions;
    }

    @Nonnull
    public String getDisplayName() {
        return this.displayName;
    }

    @Nonnull
    public I getIcon() {
        return this.icon;
    }

    public double getLengthMax() {
        return this.lengthMax;
    }

    public double getLengthMin() {
        return this.lengthMin;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public FishRarity<A, B, H> getRarity() {
        return this.rarity;
    }

    public boolean hasCatchFirework() {
        return this.hasCatchFirework;
    }

    public boolean hasNotFishItemFormat() {
        return this.hasNotFishItemFormat;
    }

    public boolean noDisplay() {
        return this.noDisplay;
    }
}
