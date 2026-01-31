package me.elsiff.morefish.item;

import me.elsiff.morefish.fish.FishIcon;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FishTypeTagType implements PersistentDataType<PersistentDataContainer, FishType> {

    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public Class<FishType> getComplexType() {
        return FishType.class;
    }

    @Override
    public PersistentDataContainer toPrimitive(FishType complex, PersistentDataAdapterContext context) {
        PersistentDataContainer main = context.newPersistentDataContainer();
        TagKey.NAME.setValue(main, complex.name());
        TagKey.DISPLAY_NAME.setValue(main, complex.displayName());
        TagKey.PRICE_MULTIPLIER.setValue(main, complex.priceMultiplier());
        TagKey.PRICE_MULTIPLIER.setValue(main, complex.priceMultiplier());
        TagKey.FISH_RARITY.setValue(main, complex.rarity());
        return main;
    }

    @Override
    public FishType fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context) {
        String name = TagKey.NAME.getValue(primitive);
        String displayName = TagKey.DISPLAY_NAME.getValue(primitive);
        float additionalPrice = TagKey.PRICE_MULTIPLIER.getValue(primitive);
        FishRarity rarity = TagKey.FISH_RARITY.getValue(primitive);
        return new FishType(name, rarity, displayName, additionalPrice, new FishIcon(new ItemStack(Material.SALMON)));
    }
}
