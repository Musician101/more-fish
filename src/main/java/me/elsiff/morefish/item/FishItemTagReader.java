package me.elsiff.morefish.item;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import me.elsiff.morefish.fishing.FishTypeTable;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

public final class FishItemTagReader {

    private final NamespacedKey fishLengthKey;
    private final NamespacedKey fishTypeKey;
    private final FishTypeTable fishTypeTable;

    public FishItemTagReader(@Nonnull FishTypeTable fishTypeTable, @Nonnull NamespacedKey fishTypeKey, @Nonnull NamespacedKey fishLengthKey) {
        this.fishTypeTable = fishTypeTable;
        this.fishTypeKey = fishTypeKey;
        this.fishLengthKey = fishLengthKey;
    }

    public final boolean canRead(@Nonnull ItemMeta itemMeta) {
        CustomItemTagContainer tags = itemMeta.getCustomTagContainer();
        return tags.hasCustomTag(fishTypeKey, ItemTagType.STRING) && tags.hasCustomTag(fishLengthKey, ItemTagType.DOUBLE);
    }

    @Nonnull
    public final Fish read(@Nonnull ItemMeta itemMeta) {
        CustomItemTagContainer tags = itemMeta.getCustomTagContainer();
        if (!tags.hasCustomTag(fishTypeKey, ItemTagType.STRING)) {
            throw new IllegalArgumentException("Item meta must have fish type tag");
        }

        if (!tags.hasCustomTag(fishLengthKey, ItemTagType.DOUBLE)) {
            throw new IllegalArgumentException("Item meta must have fish length tag");
        }

        String typeName = tags.getCustomTag(fishTypeKey, ItemTagType.STRING);
        FishType type = fishTypeTable.getTypes().stream().filter(it -> typeName.equals(it.getName())).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist"));
        Double length = tags.getCustomTag(fishLengthKey, ItemTagType.DOUBLE);
        return new Fish(type, length);
    }
}
