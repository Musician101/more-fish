package me.elsiff.morefish.item;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import me.elsiff.morefish.fishing.FishTypeTable;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

final class FishItemTagReader {

    private final NamespacedKey fishLengthKey;
    private final NamespacedKey fishTypeKey;
    private final FishTypeTable fishTypeTable;

    public FishItemTagReader(@Nonnull FishTypeTable fishTypeTable, @Nonnull NamespacedKey fishTypeKey, @Nonnull NamespacedKey fishLengthKey) {
        this.fishTypeTable = fishTypeTable;
        this.fishTypeKey = fishTypeKey;
        this.fishLengthKey = fishLengthKey;
    }

    public final boolean canRead(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        return tags.has(fishTypeKey, PersistentDataType.STRING) && tags.has(fishLengthKey, PersistentDataType.DOUBLE);
    }

    @Nonnull
    public final Fish read(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        if (!tags.has(fishTypeKey, PersistentDataType.STRING)) {
            throw new IllegalArgumentException("Item meta must have fish type tag");
        }

        if (!tags.has(fishLengthKey, PersistentDataType.DOUBLE)) {
            throw new IllegalArgumentException("Item meta must have fish length tag");
        }

        String typeName = tags.get(fishTypeKey, PersistentDataType.STRING);
        FishType type = fishTypeTable.getTypes().stream().filter(it -> typeName.equals(it.getName())).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist"));
        Double length = tags.get(fishLengthKey, PersistentDataType.DOUBLE);
        return new Fish(type, length);
    }
}
