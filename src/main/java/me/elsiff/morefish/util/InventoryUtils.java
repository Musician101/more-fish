package me.elsiff.morefish.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtils {

    private InventoryUtils() {
    }

    public static void deliverTo(@Nonnull Inventory inventory, @Nonnull ItemStack delivery, @Nonnull List<Integer> acceptableSlots) {
        List<ItemStack> contents = acceptableSlots.stream().map(inventory::getItem).filter(Objects::nonNull).filter(itemStack -> itemStack.getType() != Material.AIR).collect(Collectors.toList());
        contents.stream().filter(it -> it.isSimilar(delivery)).forEach(itemStack -> {
            int givingAmount = Math.min(delivery.getAmount(), itemStack.getMaxStackSize() - itemStack.getAmount());
            itemStack.setAmount(itemStack.getAmount() + givingAmount);
            delivery.setAmount(delivery.getAmount() - givingAmount);
        });

        acceptableSlots.stream().filter(slot -> InventoryExtension.isEmptyAt(inventory, slot)).forEach(slot -> {
            int placingAmount = Math.min(delivery.getAmount(), delivery.getMaxStackSize());
            ItemStack deliveryClone = delivery.clone();
            deliveryClone.setAmount(placingAmount);
            inventory.setItem(slot, deliveryClone);
            delivery.setAmount(delivery.getAmount() - placingAmount);
        });
    }

    public static void deliverTo(@Nonnull Inventory inventory, @Nonnull ItemStack delivery) {
        deliverTo(inventory, delivery, InventoryExtension.slots(inventory));
    }

    @Nonnull
    public static ItemStack emptyStack() {
        return new ItemStack(Material.AIR);
    }
}
