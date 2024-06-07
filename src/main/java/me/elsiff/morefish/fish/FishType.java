package me.elsiff.morefish.fish;

import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fish.catchhandler.CatchHandler;
import me.elsiff.morefish.fish.condition.FishCondition;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public final class FishType implements Comparable<FishType> {

    private final @NotNull String name;
    private final @NotNull FishRarity rarity;
    private final @NotNull String displayName;
    private final double lengthMin;
    private final double lengthMax;
    private final @NotNull ItemStack icon;
    private final @NotNull List<CatchHandler> catchHandlers;
    private final @NotNull PlayerAnnouncement catchAnnouncement;
    private final @NotNull List<FishCondition> conditions;
    private final @NotNull Map<Integer, Double> luckOfTheSeaChances;
    private final boolean hasNotFishItemFormat;
    private final boolean noDisplay;
    private final boolean hasCatchFirework;
    private final double additionalPrice;

    public FishType(@NotNull String name, @NotNull FishRarity rarity, @NotNull String displayName, double additionalPrice) {
        this(name, rarity, displayName, 0, 1, new ItemStack(Material.SALMON), List.of(), PlayerAnnouncement.empty(), List.of(), Map.of(), false, false, false, additionalPrice);
    }

    public FishType(@NotNull String name, @NotNull FishRarity rarity, @NotNull String displayName, double lengthMin,
                    double lengthMax, @NotNull ItemStack icon, @NotNull List<CatchHandler> catchHandlers,
                    @NotNull PlayerAnnouncement catchAnnouncement, @NotNull List<FishCondition> conditions,
                    @NotNull Map<Integer, Double> luckOfTheSeaChances,
                    boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework,
                    double additionalPrice) {
        this.name = name;
        this.rarity = rarity;
        this.displayName = displayName;
        this.lengthMin = lengthMin;
        this.lengthMax = lengthMax;
        this.icon = icon;
        this.catchHandlers = catchHandlers;
        this.catchAnnouncement = catchAnnouncement;
        this.conditions = conditions;
        this.luckOfTheSeaChances = luckOfTheSeaChances;
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

    @NotNull
    public Fish generateFish() {
        if (lengthMin > lengthMax) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = lengthMin + new Random().nextDouble() * (lengthMax - lengthMin);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), lengthMin, lengthMax);
        return new Fish(this, length);
    }

    @Override
    public int compareTo(@NotNull FishType o) {
        return name.compareTo(o.name);
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull FishRarity rarity() {
        return rarity;
    }

    public @NotNull String displayName() {
        return displayName;
    }

    public double lengthMin() {
        return lengthMin;
    }

    public double lengthMax() {
        return lengthMax;
    }

    public @NotNull ItemStack icon() {
        return icon;
    }

    public @NotNull List<CatchHandler> catchHandlers() {
        return catchHandlers;
    }

    public @NotNull PlayerAnnouncement catchAnnouncement() {
        return catchAnnouncement;
    }

    public @NotNull List<FishCondition> conditions() {
        return conditions;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (FishType) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.rarity, that.rarity) &&
                Objects.equals(this.displayName, that.displayName) &&
                Double.doubleToLongBits(this.lengthMin) == Double.doubleToLongBits(that.lengthMin) &&
                Double.doubleToLongBits(this.lengthMax) == Double.doubleToLongBits(that.lengthMax) &&
                Objects.equals(this.icon, that.icon) &&
                Objects.equals(this.catchHandlers, that.catchHandlers) &&
                Objects.equals(this.catchAnnouncement, that.catchAnnouncement) &&
                Objects.equals(this.conditions, that.conditions) &&
                Objects.equals(this.luckOfTheSeaChances, that.luckOfTheSeaChances) &&
                this.hasNotFishItemFormat == that.hasNotFishItemFormat &&
                this.noDisplay == that.noDisplay &&
                this.hasCatchFirework == that.hasCatchFirework &&
                Double.doubleToLongBits(this.additionalPrice) == Double.doubleToLongBits(that.additionalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rarity, displayName, lengthMin, lengthMax, icon, catchHandlers, catchAnnouncement, conditions, luckOfTheSeaChances, hasNotFishItemFormat, noDisplay, hasCatchFirework, additionalPrice);
    }

    @Override
    public String toString() {
        return "FishType[" +
                "name=" + name + ", " +
                "rarity=" + rarity + ", " +
                "displayName=" + displayName + ", " +
                "lengthMin=" + lengthMin + ", " +
                "lengthMax=" + lengthMax + ", " +
                "icon=" + icon + ", " +
                "catchHandlers=" + catchHandlers + ", " +
                "catchAnnouncement=" + catchAnnouncement + ", " +
                "conditions=" + conditions + ", " +
                "luckOfTheSeaChances=" + luckOfTheSeaChances + ", " +
                "hasNotFishItemFormat=" + hasNotFishItemFormat + ", " +
                "noDisplay=" + noDisplay + ", " +
                "hasCatchFirework=" + hasCatchFirework + ", " +
                "additionalPrice=" + additionalPrice + ']';
    }

}
