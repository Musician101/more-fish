package me.elsiff.morefish.item;

import me.elsiff.morefish.fish.Fish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface FishItemStackUtil {

    static Fish fish(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        return TagKey.FISH.getValue(tags);
    }

    static boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        return TagKey.FISH.isPresent(itemStack);
    }
}
