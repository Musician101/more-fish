package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public record SpigotEnchantmentCondition(Enchantment enchantment, int minLevel) implements FishCondition {

    @SuppressWarnings("ConstantConditions")
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        ItemStack fishingRod = Bukkit.getPlayer(fisher).getInventory().getItemInMainHand();
        if (fishingRod.getType() == Material.AIR) {
            return false;
        }

        return fishingRod.containsEnchantment(enchantment) && fishingRod.getEnchantmentLevel(enchantment) >= minLevel;
    }
}
