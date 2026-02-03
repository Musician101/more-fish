package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.LuckOfTheSeaModifier;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.NonRequiredKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class FishRaritySerializer extends FishAbstractSerializer<FishRarity> {

    private static final NonRequiredKey<TextColor> COLOR = ConfigKey.nonRequiredKey("color", TextColor.class, NamedTextColor.WHITE);
    private static final NonRequiredKey<Boolean> FILTER_DEFAULT_ENABLED = ConfigKey.nonRequiredKey("filter-default-enabled", Boolean.class, false);
    private static final NonRequiredKey<LuckOfTheSeaModifier> LUCK_OF_THE_SEA_MODIFIER = ConfigKey.nonRequiredKey("luck-of-the-sea-modifier", LuckOfTheSeaModifier.class, LuckOfTheSeaModifier.NONE);
    private static final RequiredKey<Integer> WEIGHT = ConfigKey.requiredKey("weight", Integer.class);

    @Override
    public FishRarity deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishRarity rarity = deserialize(node, FishRarity::new);
        rarity.color(COLOR.get(node));
        rarity.filterDefaultEnabled(FILTER_DEFAULT_ENABLED.get(node));
        rarity.luckOfTheSeaModifier(LUCK_OF_THE_SEA_MODIFIER.get(node));
        rarity.weight(WEIGHT.get(node));
        return rarity;
    }

    @Override
    public void serialize(Type type, @Nullable FishRarity obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            super.serialize(type, obj, node);
            COLOR.set(node, obj.color());
            WEIGHT.set(node, obj.weight());
            FILTER_DEFAULT_ENABLED.set(node, obj.filterDefaultEnabled());
            LUCK_OF_THE_SEA_MODIFIER.set(node, obj.luckOfTheSeaModifier());
        }
    }
}
