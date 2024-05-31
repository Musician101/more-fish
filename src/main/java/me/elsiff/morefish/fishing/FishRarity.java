package me.elsiff.morefish.fishing;

import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.condition.FishCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class FishRarity implements Comparable<FishRarity> {

    private final @NotNull String name;
    private final @NotNull String displayName;
    private final boolean isDefault;
    private final double probability;
    private final @NotNull String color;
    private final @NotNull List<CatchHandler> catchHandlers;
    private final @NotNull List<FishCondition> conditions;
    private final @NotNull PlayerAnnouncement catchAnnouncement;
    private final @NotNull Map<Integer, Double> luckOfTheSeaChances;
    private final boolean hasNotFishItemFormat;
    private final boolean noDisplay;
    private final boolean hasCatchFirework;
    private final double additionalPrice;
    private final int customModelData;
    private final boolean filterDefaultEnabled;

    public FishRarity(@NotNull String name, @NotNull String displayName, boolean isDefault, double probability,
                      @NotNull String color, @NotNull List<CatchHandler> catchHandlers,
                      @NotNull List<FishCondition> conditions, @NotNull PlayerAnnouncement catchAnnouncement,
                      @NotNull Map<Integer, Double> luckOfTheSeaChances,
                      boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework,
                      double additionalPrice, int customModelData, boolean filterDefaultEnabled) {
        this.name = name;
        this.displayName = displayName;
        this.isDefault = isDefault;
        this.probability = probability;
        this.color = color;
        this.catchHandlers = catchHandlers;
        this.conditions = conditions;
        this.catchAnnouncement = catchAnnouncement;
        this.luckOfTheSeaChances = luckOfTheSeaChances;
        this.hasNotFishItemFormat = hasNotFishItemFormat;
        this.noDisplay = noDisplay;
        this.hasCatchFirework = hasCatchFirework;
        this.additionalPrice = additionalPrice;
        this.customModelData = customModelData;
        this.filterDefaultEnabled = filterDefaultEnabled;
    }

    public FishRarity(@NotNull String name, @NotNull String displayName, @NotNull String color, double additionalPrice) {
        this(name, displayName, false, 0, color, List.of(), List.of(), PlayerAnnouncement.empty(), Map.of(), false, false, false, additionalPrice, 0, false);
    }

    @Override
    public int compareTo(@NotNull FishRarity o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull String displayName() {
        return displayName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public double probability() {
        return probability;
    }

    public @NotNull String color() {
        return color;
    }

    public @NotNull List<CatchHandler> catchHandlers() {
        return catchHandlers;
    }

    public @NotNull List<FishCondition> conditions() {
        return conditions;
    }

    public @NotNull PlayerAnnouncement catchAnnouncement() {
        return catchAnnouncement;
    }

    public @NotNull Map<Integer, Double> luckOfTheSeaChances() {
        return luckOfTheSeaChances;
    }

    public boolean hasNotFishItemFormat() {
        return hasNotFishItemFormat;
    }

    public boolean noDisplay() {
        return noDisplay;
    }

    public boolean hasCatchFirework() {
        return hasCatchFirework;
    }

    public double additionalPrice() {
        return additionalPrice;
    }

    public int customModelData() {
        return customModelData;
    }

    public boolean filterDefaultEnabled() {
        return filterDefaultEnabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (FishRarity) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.displayName, that.displayName) &&
                this.isDefault == that.isDefault &&
                Double.doubleToLongBits(this.probability) == Double.doubleToLongBits(that.probability) &&
                Objects.equals(this.color, that.color) &&
                Objects.equals(this.catchHandlers, that.catchHandlers) &&
                Objects.equals(this.conditions, that.conditions) &&
                Objects.equals(this.catchAnnouncement, that.catchAnnouncement) &&
                Objects.equals(this.luckOfTheSeaChances, that.luckOfTheSeaChances) &&
                this.hasNotFishItemFormat == that.hasNotFishItemFormat &&
                this.noDisplay == that.noDisplay &&
                this.hasCatchFirework == that.hasCatchFirework &&
                Double.doubleToLongBits(this.additionalPrice) == Double.doubleToLongBits(that.additionalPrice) &&
                this.customModelData == that.customModelData &&
                this.filterDefaultEnabled == that.filterDefaultEnabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayName, isDefault, probability, color, catchHandlers, conditions, catchAnnouncement, luckOfTheSeaChances, hasNotFishItemFormat, noDisplay, hasCatchFirework, additionalPrice, customModelData, filterDefaultEnabled);
    }

    @Override
    public String toString() {
        return "FishRarity[" +
                "name=" + name + ", " +
                "displayName=" + displayName + ", " +
                "isDefault=" + isDefault + ", " +
                "probability=" + probability + ", " +
                "color=" + color + ", " +
                "catchHandlers=" + catchHandlers + ", " +
                "conditions=" + conditions + ", " +
                "catchAnnouncement=" + catchAnnouncement + ", " +
                "luckOfTheSeaChances=" + luckOfTheSeaChances + ", " +
                "hasNotFishItemFormat=" + hasNotFishItemFormat + ", " +
                "noDisplay=" + noDisplay + ", " +
                "hasCatchFirework=" + hasCatchFirework + ", " +
                "additionalPrice=" + additionalPrice + ", " +
                "customModelData=" + customModelData + ", " +
                "filterDefaultEnabled=" + filterDefaultEnabled + ']';
    }

}
