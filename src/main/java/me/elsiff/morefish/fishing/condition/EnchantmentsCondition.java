package me.elsiff.morefish.fishing.condition;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record EnchantmentsCondition(@NotNull Map<Enchantment, Integer> enchantments) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher) {
        ItemStack fishingRod = fisher.getInventory().getItemInMainHand();
        if (fishingRod.getType() == Material.AIR) {
            return false;
        }

        return enchantments.entrySet().stream().allMatch(e -> fishingRod.containsEnchantment(e.getKey()) && fishingRod.getEnchantmentLevel(e.getKey()) >= e.getValue());
    }
}
