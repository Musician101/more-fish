package me.elsiff.morefish.fish;

import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class FishRarity extends FishAbstract<FishRarity> {

    private int weight = 1;
    private TextColor color;
    private boolean filterDefaultEnabled = false;
    private LuckOfTheSeaModifier luckOfTheSeaModifier = LuckOfTheSeaModifier.NONE;

    public FishRarity(NamespacedKey key, String displayName) {
        super("fish-rarity", key, displayName);
        this.color = NamedTextColor.WHITE;
    }

    public FishRarity(NamespacedKey key, String displayName, TextColor color, float priceMultiplier) {
        super("fish-rarity", key, displayName, priceMultiplier);
        this.color = color;
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            String value = arguments.popOr("fish-rarity needs at least 1 argument.").value();
            return switch (value) {
                case "color" -> {
                    if (arguments.hasNext() && arguments.pop().isTrue()) {
                        yield Tag.selfClosingInserting(Component.text(color.asHexString(), color));
                    }

                    yield Tag.styling(b -> b.color(color));
                }
                case "filter-default-enabled" ->
                        TagResolverUtil.booleanTag(value, filterDefaultEnabled, arguments, ctx);
                case "luck-of-the-sea-modifier" ->
                        TagResolverUtil.fromResolver(luckOfTheSeaModifier, value, arguments, ctx);
                case "weight" -> TagResolverUtil.numberTag(value, weight, arguments, ctx);
                default -> super.resolve(name, arguments, ctx);
            };
        }

        return null;
    }

    public void weight(int weight) {
        this.weight = weight;
    }

    public int modifiedWeight(int luckOfTheSeaLevel) {
        float modifier = luckOfTheSeaModifier.amount() * luckOfTheSeaLevel;
        return switch (luckOfTheSeaModifier.type()) {
            case FLAT -> weight + (int) modifier;
            case PERCENTAGE -> (int) Math.ceil(weight + (weight * modifier));
        };
    }

    public void color(TextColor color) {
        this.color = color;
    }

    public void filterDefaultEnabled(boolean filterDefaultEnabled) {
        this.filterDefaultEnabled = filterDefaultEnabled;
    }

    @Override
    public int compareTo(FishRarity o) {
        return Integer.compare(this.weight, o.weight);
    }

    public int weight() {
        return weight;
    }

    public TextColor color() {
        return color;
    }

    public boolean filterDefaultEnabled() {
        return filterDefaultEnabled;
    }

    public LuckOfTheSeaModifier luckOfTheSeaModifier() {
        return luckOfTheSeaModifier;
    }

    public void luckOfTheSeaModifier(LuckOfTheSeaModifier luckOfTheSeaModifier) {
        this.luckOfTheSeaModifier = luckOfTheSeaModifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FishRarity rarity) {
            return key.equals(rarity.getKey());
        }

        return false;
    }
}
