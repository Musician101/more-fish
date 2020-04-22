package me.elsiff.morefish.fishing;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import org.bukkit.ChatColor;

public final class FishRarity implements Comparable<FishRarity> {

    private final double additionalPrice;
    @Nonnull
    private final PlayerAnnouncement catchAnnouncement;
    @Nonnull
    private final List<CatchHandler> catchHandlers;
    @Nonnull
    private final ChatColor color;
    @Nonnull
    private final String displayName;
    private final boolean hasCatchFirework;
    private final boolean hasNotFishItemFormat;
    private final boolean isDefault;
    @Nonnull
    private final String name;
    private final boolean noDisplay;
    private final double probability;

    public FishRarity(@Nonnull String name, @Nonnull String displayName, boolean var3, double probability, @Nonnull ChatColor color, @Nonnull List<CatchHandler> catchHandlers, @Nonnull PlayerAnnouncement catchAnnouncement, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {
        this.name = name;
        this.displayName = displayName;
        this.isDefault = var3;
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
    public int compareTo(@Nonnull FishRarity o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
    }

    @Nonnull
    public final FishRarity copy(@Nonnull String name, @Nonnull String displayName, boolean var3, double probability, @Nonnull ChatColor color, @Nonnull List<CatchHandler> catchHandlers, @Nonnull PlayerAnnouncement catchAnnouncement, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) {
        return new FishRarity(name, displayName, var3, probability, color, catchHandlers, catchAnnouncement, hasNotFishItemFormat, noDisplay, hasCatchFirework, additionalPrice);
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
    public final ChatColor getColor() {
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
