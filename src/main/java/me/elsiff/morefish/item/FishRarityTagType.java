package me.elsiff.morefish.item;

import me.elsiff.morefish.fishing.FishRarity;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FishRarityTagType implements PersistentDataType<PersistentDataContainer, FishRarity> {

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<FishRarity> getComplexType() {
        return FishRarity.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull FishRarity complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer main = context.newPersistentDataContainer();
        TagKey.NAME.setValue(main, complex.name());
        TagKey.DISPLAY_NAME.setValue(main, complex.displayName());
        TagKey.COLOR.setValue(main, complex.color());
        TagKey.ADDITIONAL_PRICE.setValue(main, complex.additionalPrice());
        return main;
    }

    @Override
    public @NotNull FishRarity fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        String name = TagKey.NAME.getValue(primitive);
        String displayName = TagKey.DISPLAY_NAME.getValue(primitive);
        String color = TagKey.COLOR.getValue(primitive);
        double additionalPrice = TagKey.ADDITIONAL_PRICE.getValue(primitive);
        return new FishRarity(name, displayName, color, additionalPrice);
    }
}
