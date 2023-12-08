package me.elsiff.morefish.common.fishing;

import java.util.List;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import org.jetbrains.annotations.NotNull;

public abstract class FishType<C extends FishingCompetition<F>, F extends Fish<?>, I, P, S> {

    protected final double lengthMax;
    protected final double lengthMin;
    private final double additionalPrice;
    private final @NotNull PlayerAnnouncement<P> catchAnnouncement;
    private final @NotNull List<CatchHandler<F, P>> catchHandlers;
    private final @NotNull List<FishCondition<C, I, P>> conditions;
    private final @NotNull String displayName;
    private final boolean hasCatchFirework;
    private final boolean hasNotFishItemFormat;
    private final @NotNull S icon;
    private final @NotNull String name;
    private final boolean noDisplay;
    private final @NotNull FishRarity<C, F, I, P> rarity;

    public FishType(@NotNull String name, @NotNull FishRarity<C, F, I, P> rarity, @NotNull String displayName, double lengthMin, double lengthMax, @NotNull S icon, @NotNull List<CatchHandler<F, P>> catchHandlers, @NotNull PlayerAnnouncement<P> catchAnnouncement, @NotNull List<FishCondition<C, I, P>> conditions, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {
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

    public double additionalPrice() {
        return additionalPrice;
    }

    public @NotNull PlayerAnnouncement<P> catchAnnouncement() {
        return catchAnnouncement;
    }

    public @NotNull List<CatchHandler<F, P>> catchHandlers() {
        return catchHandlers;
    }

    protected double clamp(double value, double min, double max) {
        double var7 = Math.min(value, max);
        return Math.max(var7, min);
    }

    public @NotNull List<FishCondition<C, I, P>> conditions() {
        return conditions;
    }

    public @NotNull String displayName() {
        return displayName;
    }

    protected double floorToTwoDecimalPlaces(double value) {
        double var3 = value * (double) 10;
        return Math.floor(var3) / (double) 10;
    }

    @NotNull
    public abstract F generateFish();

    public boolean hasCatchFirework() {
        return hasCatchFirework;
    }

    public boolean hasNotFishItemFormat() {
        return hasNotFishItemFormat;
    }

    public @NotNull S icon() {
        return icon;
    }

    public double lengthMax() {
        return lengthMax;
    }

    public double lengthMin() {
        return lengthMin;
    }

    public @NotNull String name() {
        return name;
    }

    public boolean noDisplay() {
        return noDisplay;
    }

    public @NotNull FishRarity<C, F, I, P> rarity() {
        return rarity;
    }
}
