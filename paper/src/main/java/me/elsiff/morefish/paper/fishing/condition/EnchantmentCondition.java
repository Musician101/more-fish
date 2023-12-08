package me.elsiff.morefish.paper.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record EnchantmentCondition(@NotNull Enchantment enchantment,
                                   int minLevel) implements FishCondition<PaperFishingCompetition, Item, Player> {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull PaperFishingCompetition fishingCompetition) {
        ItemStack fishingRod = fisher.getInventory().getItemInMainHand();
        if (fishingRod.getType() == Material.AIR) {
            return false;
        }

        return fishingRod.containsEnchantment(enchantment) && fishingRod.getEnchantmentLevel(enchantment) >= minLevel;
    }
}
