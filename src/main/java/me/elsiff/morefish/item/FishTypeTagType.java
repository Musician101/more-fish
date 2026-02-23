package me.elsiff.morefish.item;

import me.elsiff.morefish.fish.FishIcon;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
        TagKey.ID.setValue(main, complex.getKey().asString());
        TagKey.DISPLAY_NAME.setValue(main, complex.displayName());
        TagKey.PRICE_MULTIPLIER.setValue(main, complex.priceMultiplier());
        TagKey.PRICE_MULTIPLIER.setValue(main, complex.priceMultiplier());
        TagKey.FISH_RARITY.setValue(main, complex.rarity());
        return main;
    }

    @Override
    public FishType fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context) {
        String id = TagKey.ID.getValue(primitive);
        String displayName = TagKey.DISPLAY_NAME.getValue(primitive);
        float additionalPrice = TagKey.PRICE_MULTIPLIER.getValue(primitive);
        FishRarity rarity = TagKey.FISH_RARITY.getValue(primitive);
        // If this is ever null, then someone touched the NBT data
        //noinspection DataFlowIssue
        return new FishType(NamespacedKey.fromString(id), rarity, displayName, additionalPrice, new FishIcon(new ItemStack(Material.SALMON)));
    }
}
