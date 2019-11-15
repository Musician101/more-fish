package me.elsiff.morefish.item;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

final class FishItemTagWriter {

    private final NamespacedKey fishLengthKey;
    private final NamespacedKey fishTypeKey;

    public FishItemTagWriter(@Nonnull NamespacedKey fishTypeKey, @Nonnull NamespacedKey fishLengthKey) {
        super();
        this.fishTypeKey = fishTypeKey;
        this.fishLengthKey = fishLengthKey;
    }

    public final void write(@Nonnull ItemMeta itemMeta, @Nonnull Fish fish) {
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        data.set(this.fishTypeKey, PersistentDataType.STRING.STRING, fish.getType().getName());
        data.set(this.fishLengthKey, PersistentDataType.DOUBLE, fish.getLength());
    }
}
