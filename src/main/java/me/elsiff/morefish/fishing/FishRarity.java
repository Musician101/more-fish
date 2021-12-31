package me.elsiff.morefish.fishing;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import org.bukkit.ChatColor;

public record FishRarity(@Nonnull String name, @Nonnull String displayName, boolean isDefault, double probability, @Nonnull ChatColor color, @Nonnull List<CatchHandler> catchHandlers, @Nonnull PlayerAnnouncement catchAnnouncement, boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework, double additionalPrice) implements Comparable<FishRarity> {

    @Override
    public int compareTo(@Nonnull FishRarity o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
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
    public ChatColor getColor() {
        return this.color;
    }

    @Nonnull
    public String getDisplayName() {
        return this.displayName;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public boolean getNoDisplay() {
        return this.noDisplay;
    }

    public double getProbability() {
        return this.probability;
    }
}
