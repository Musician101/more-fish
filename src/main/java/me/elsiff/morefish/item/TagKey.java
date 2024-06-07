package me.elsiff.morefish.item;

import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.elsiff.morefish.MoreFish.getPlugin;

public interface TagKey<P, C> {

    TagKey<Double, Double> ADDITIONAL_PRICE = new TagKey<>() {
        @Override
        public @NotNull NamespacedKey key() {
            return new NamespacedKey(getPlugin(), "additional-price");
        }

        @Override
        public @NotNull PersistentDataType<Double, Double> dataType() {
            return PersistentDataType.DOUBLE;
        }
    };
    TagKey<Double, Double> LENGTH = new TagKey<>() {
        @Override
        public @NotNull NamespacedKey key() {
            return new NamespacedKey(getPlugin(), "length");
        }

        @Override
        public @NotNull PersistentDataType<Double, Double> dataType() {
            return PersistentDataType.DOUBLE;
        }
    };
    TagKey<String, String> DISPLAY_NAME = new TagKey<>() {
        @Override
        public @NotNull NamespacedKey key() {
            return new NamespacedKey(getPlugin(), "display-name");
        }

        @Override
        public @NotNull PersistentDataType<String, String> dataType() {
            return PersistentDataType.STRING;
        }
    };
    TagKey<String, String> NAME = new TagKey<>() {
        @Override
        public @NotNull NamespacedKey key() {
            return new NamespacedKey(getPlugin(), "name");
        }

        @Override
        public @NotNull PersistentDataType<String, String> dataType() {
            return PersistentDataType.STRING;
        }
    };
    TagKey<PersistentDataContainer, FishRarity> FISH_RARITY = new TagKey<>() {
        @Override
        public @NotNull NamespacedKey key() {
            return new NamespacedKey(getPlugin(), "rarity");
        }

        @Override
        public @NotNull PersistentDataType<PersistentDataContainer, FishRarity> dataType() {
            return new FishRarityTagType();
        }
    };
    TagKey<PersistentDataContainer, FishType> FISH_TYPE = new TagKey<>() {
        @Override
        public @NotNull NamespacedKey key() {
            return new NamespacedKey(getPlugin(), "type");
        }

        @Override
        public @NotNull PersistentDataType<PersistentDataContainer, FishType> dataType() {
            return new FishTypeTagType();
        }
    };
    TagKey<String, String> COLOR = new TagKey<>() {
        @Override
        public @NotNull NamespacedKey key() {
            return new NamespacedKey(getPlugin(), "color");
        }

        @Override
        public @NotNull PersistentDataType<String, String> dataType() {
            return PersistentDataType.STRING;
        }
    };
    TagKey<PersistentDataContainer, Fish> FISH = new TagKey<>() {
        @Override
        public @NotNull NamespacedKey key() {
            return new NamespacedKey(getPlugin(), "fish");
        }

        @Override
        public @NotNull PersistentDataType<PersistentDataContainer, Fish> dataType() {
            return new FishTagType();
        }
    };

    @NotNull
    NamespacedKey key();

    @NotNull
    PersistentDataType<P, C> dataType();

    default void setValue(@NotNull PersistentDataContainer container, @NotNull C value) {
        container.set(key(), dataType(), value);
    }

    @NotNull
    default C getValue(@NotNull PersistentDataContainer container) {
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
}
