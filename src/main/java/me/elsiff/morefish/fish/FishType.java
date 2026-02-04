package me.elsiff.morefish.fish;

import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Random;

@NullMarked
public final class FishType extends FishAbstract<FishType> {

    private FishRarity rarity;
    private double minLength = 0.1;
    private double maxLength = 1;
    private FishIcon icon;

    @SuppressWarnings("UnstableApiUsage")
    public FishType(NamespacedKey key, String displayName, FishRarity rarity) {
        this(key, rarity, displayName, new FishIcon(ItemType.SALMON.createItemStack()));
    }

    public FishType(NamespacedKey key, FishRarity rarity, String displayName, FishIcon icon) {
        super("fish-type", key, displayName);
        this.rarity = rarity;
        this.icon = icon;
    }

    public FishType(NamespacedKey key, FishRarity rarity, String displayName, float priceMultiplier, FishIcon icon) {
        super("fish-type", key, displayName, priceMultiplier);
        this.rarity = rarity;
        this.icon = icon;
    }

    public void maxLength(double maxLength) {
        this.maxLength = maxLength;
    }

    public void minLength(double minLength) {
        this.minLength = minLength;
    }

    public void icon(FishIcon icon) {
        this.icon = icon;
    }

    public void rarity(FishRarity rarity) {
        this.rarity = rarity;
    }

    private double clamp(double value, double min, double max) {
        double var7 = Math.min(value, max);
        return Math.max(var7, min);
    }

    private double floorToTwoDecimalPlaces(double value) {
        double var3 = value * 10;
        return Math.floor(var3) / 10;
    }

    public Fish generateFish(double length) {
        if (minLength > length && length > maxLength) {
            throw new IllegalArgumentException("Length is outside the min/max range for " + getKey());
        }

        return new Fish(this, length);
    }

    public Fish generateFish() {
        if (minLength > maxLength) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = minLength + new Random().nextDouble() * (maxLength - minLength);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), minLength, maxLength);
        return new Fish(this, length);
    }

    @Override
    public int compareTo(FishType o) {
        return getKey().compareTo(o.getKey());
    }

    public FishRarity rarity() {
        return rarity;
    }

    public double minLength() {
        return minLength;
    }

    public double maxLength() {
        return maxLength;
    }

    public FishIcon icon() {
        return icon;
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            String value = arguments.popOr("fish-type needs at least 1 argument.").value();
            return switch (value) {
                case "fish-rarity" -> Tag.preProcessParsed(rarity.getKey().asString());
                case "max-length" -> TagResolverUtil.numberTag("max-length", maxLength, arguments, ctx);
                case "min-length" -> TagResolverUtil.numberTag("min-length", minLength, arguments, ctx);
                case "name-with-rarity" ->
                        Tag.preProcessParsed((noDisplay() ? "" : rarity().displayName().toUpperCase() + " ") + displayName());
                default -> super.resolve(name, arguments, ctx);
            };
        }

        return null;
    }
}
