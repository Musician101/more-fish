package me.elsiff.morefish.item;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

public final class FishItemTagWriter {

    private final NamespacedKey fishLengthKey;
    private final NamespacedKey fishTypeKey;

    public FishItemTagWriter(@Nonnull NamespacedKey fishTypeKey, @Nonnull NamespacedKey fishLengthKey) {
        super();
        this.fishTypeKey = fishTypeKey;
        this.fishLengthKey = fishLengthKey;
    }

    public final void write(@Nonnull ItemMeta itemMeta, @Nonnull Fish fish) {
        CustomItemTagContainer var3 = itemMeta.getCustomTagContainer();
        var3.setCustomTag(this.fishTypeKey, ItemTagType.STRING, fish.getType().getName());
        var3.setCustomTag(this.fishLengthKey, ItemTagType.DOUBLE, fish.getLength());
    }
}
