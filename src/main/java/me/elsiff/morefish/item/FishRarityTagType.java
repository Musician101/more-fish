package me.elsiff.morefish.item;

import me.elsiff.morefish.fish.FishRarity;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FishRarityTagType implements PersistentDataType<PersistentDataContainer, FishRarity> {

    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public Class<FishRarity> getComplexType() {
        return FishRarity.class;
    }

    @Override
    public PersistentDataContainer toPrimitive(FishRarity complex, PersistentDataAdapterContext context) {
        PersistentDataContainer main = context.newPersistentDataContainer();
        TagKey.NAME.setValue(main, complex.name());
        TagKey.DISPLAY_NAME.setValue(main, complex.displayName());
        TagKey.COLOR.setValue(main, complex.color().asHexString());
        TagKey.PRICE_MULTIPLIER.setValue(main, complex.priceMultiplier());
        return main;
    }

    @Override
    public FishRarity fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context) {
        String name = TagKey.NAME.getValue(primitive);
        String displayName = TagKey.DISPLAY_NAME.getValue(primitive);
        String color = TagKey.COLOR.getValue(primitive);
        TextColor textColor = TextColor.fromHexString(color);
        if (textColor == null) {
            textColor = NamedTextColor.WHITE;
        }

        float additionalPrice = TagKey.PRICE_MULTIPLIER.getValue(primitive);
        return new FishRarity(name, displayName, textColor, additionalPrice);
    }
}
