package io.musician101.morefish.common.fishing;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import java.util.List;
import javax.annotation.Nonnull;

public class FishRarity<A extends PlayerAnnouncement<?>, C, H extends CatchHandler<?, ?>> implements Comparable<FishRarity<A, C, H>> {

    private final double additionalPrice;
    @Nonnull
    private final A catchAnnouncement;
    @Nonnull
    private final List<H> catchHandlers;
    @Nonnull
    private final C color;
    @Nonnull
    private final String displayName;
    private final boolean hasCatchFirework;
    private final boolean hasNotFishItemFormat;
    private final boolean isDefault;
    @Nonnull
    private final String name;
    private final boolean noDisplay;
    private final double probability;

    public FishRarity(@Nonnull String name, @Nonnull String displayName, boolean isDefault, double probability, @Nonnull C color, @Nonnull List<H> catchHandlers, @Nonnull A catchAnnouncement, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {
        this.name = name;
        this.displayName = displayName;
        this.isDefault = isDefault;
        this.probability = probability;
        this.color = color;
        this.catchHandlers = catchHandlers;
        this.catchAnnouncement = catchAnnouncement;
        this.hasNotFishItemFormat = hasNotFishItemFormat;
        this.noDisplay = noDisplay;
        this.hasCatchFirework = hasCatchFirework;
        this.additionalPrice = additionalPrice;
    }

    @Override
    public int compareTo(@Nonnull FishRarity<A, C, H> o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
    }

    public final double getAdditionalPrice() {
        return this.additionalPrice;
    }

    @Nonnull
    public final A getCatchAnnouncement() {
        return this.catchAnnouncement;
    }

    @Nonnull
    public final List<H> getCatchHandlers() {
        return this.catchHandlers;
    }

    @Nonnull
    public final C getColor() {
        return this.color;
    }

    @Nonnull
    public final String getDisplayName() {
        return this.displayName;
    }

    @Nonnull
    public final String getName() {
        return this.name;
    }

    public final boolean getNoDisplay() {
        return this.noDisplay;
    }

    public final double getProbability() {
        return this.probability;
    }

    public final boolean hasCatchFirework() {
        return this.hasCatchFirework;
    }

    public final boolean hasNotFishItemFormat() {
        return this.hasNotFishItemFormat;
    }

    public final boolean isDefault() {
        return isDefault;
    }
}
