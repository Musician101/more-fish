package me.elsiff.morefish.item;

import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public interface TagKey<P, C> {

    TagKey<Float, Float> PRICE_MULTIPLIER = tagKey("price-multiplier", PersistentDataType.FLOAT);
    TagKey<Double, Double> LENGTH = tagKey("length", PersistentDataType.DOUBLE);
    TagKey<String, String> DISPLAY_NAME = tagKey("display-name", PersistentDataType.STRING);
    TagKey<String, String> NAME = tagKey("name", PersistentDataType.STRING);
    TagKey<PersistentDataContainer, FishRarity> FISH_RARITY = tagKey("rarity", new FishRarityTagType());
    TagKey<PersistentDataContainer, FishType> FISH_TYPE = tagKey("type", new FishTypeTagType());
    TagKey<String, String> COLOR = tagKey("color", PersistentDataType.STRING);
    TagKey<PersistentDataContainer, Fish> FISH = tagKey("fish", new FishTagType());

    NamespacedKey key();

    PersistentDataType<P, C> dataType();

    default void setValue(PersistentDataContainer container, C value) {
        container.set(key(), dataType(), value);
    }

    default C getValue(PersistentDataContainer container) {
        C value = container.get(key(), dataType());
        if (value == null) {
            throw new IllegalArgumentException("Malformed NBT: " + container);
        }

        return value;
    }

    default boolean isPresent(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return itemStack.getItemMeta().getPersistentDataContainer().has(key(), dataType());
    }

    private static <P, C> TagKey<P, C> tagKey(String key, PersistentDataType<P, C> dataType) {
        return new TagKey<>() {

            @Override
            public NamespacedKey key() {
                return new NamespacedKey(getPlugin(), key);
            }

            @Override
            public PersistentDataType<P, C> dataType() {
                return dataType;
            }
        };
    }
}
