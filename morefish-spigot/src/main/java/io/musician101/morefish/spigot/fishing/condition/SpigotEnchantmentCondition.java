package io.musician101.morefish.spigot.fishing.condition;

import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SpigotEnchantmentCondition implements SpigotFishCondition {

    private final Enchantment enchantment;
    private final int minLevel;

    public SpigotEnchantmentCondition(@Nonnull Enchantment enchantment, int minLevel) {
        this.enchantment = enchantment;
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        ItemStack fishingRod = fisher.getInventory().getItemInMainHand();
        if (fishingRod.getType() == Material.AIR) {
            return false;
        }

        return fishingRod.containsEnchantment(enchantment) && fishingRod.getEnchantmentLevel(enchantment) >= minLevel;
    }
}
