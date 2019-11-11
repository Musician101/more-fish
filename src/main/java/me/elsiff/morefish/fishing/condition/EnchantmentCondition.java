package me.elsiff.morefish.fishing.condition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EnchantmentCondition implements FishCondition {

    private final Enchantment enchantment;
    private final int minLevel;

    public EnchantmentCondition(@Nonnull Enchantment enchantment, int minLevel) {
        super();
        this.enchantment = enchantment;
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        ItemStack fishingRod = fisher.getInventory().getItemInMainHand();
        if (fishingRod.getType() == Material.AIR) {
            return false;
        }

        return fishingRod.containsEnchantment(enchantment) && fishingRod.getEnchantmentLevel(enchantment) >= minLevel;
    }
}
