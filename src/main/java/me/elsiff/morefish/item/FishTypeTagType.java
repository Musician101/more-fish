package me.elsiff.morefish.item;

import me.elsiff.morefish.fishing.FishRarity;
import me.elsiff.morefish.fishing.FishType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FishTypeTagType implements PersistentDataType<PersistentDataContainer, FishType> {

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<FishType> getComplexType() {
        return FishType.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull FishType complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer main = context.newPersistentDataContainer();
        TagKey.NAME.setValue(main, complex.name());
        TagKey.DISPLAY_NAME.setValue(main, complex.displayName());
        TagKey.ADDITIONAL_PRICE.setValue(main, complex.additionalPrice());
        TagKey.ADDITIONAL_PRICE.setValue(main, complex.additionalPrice());
        TagKey.FISH_RARITY.setValue(main, complex.rarity());
        return main;
    }

    @Override
    public @NotNull FishType fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        String name = TagKey.NAME.getValue(primitive);
        String displayName = TagKey.DISPLAY_NAME.getValue(primitive);
        double additionalPrice = TagKey.ADDITIONAL_PRICE.getValue(primitive);
        FishRarity rarity = TagKey.FISH_RARITY.getValue(primitive);
        return new FishType(name, rarity, displayName, additionalPrice);
    }
}
