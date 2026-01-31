package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.LuckOfTheSeaModifier;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class FishRaritySerializer extends FishAbstractSerializer<FishRarity> {

    @Override
    public FishRarity deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishRarity rarity = deserialize(node, FishRarity::new);
        rarity.weight(node.node("weight").getInt());
        rarity.color(node.node("color").get(TextColor.class, NamedTextColor.WHITE));
        rarity.filterDefaultEnabled(node.node("filter-default-enabled").getBoolean());
        rarity.luckOfTheSeaModifier(node.node("luck-of-the-sea-modifier").get(LuckOfTheSeaModifier.class, LuckOfTheSeaModifier.NONE));
        return rarity;
    }

    @Override
    public void serialize(Type type, @Nullable FishRarity obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            super.serialize(type, obj, node);
            node.node("weight").set(obj.weight());
            node.node("color").set(TextColor.class, obj.color());
            node.node("filter-default-enabled").set(obj.filterDefaultEnabled());
            node.node("luck-of-the-sea-modifiers").set(obj.luckOfTheSeaModifier());
        }
    }
}
