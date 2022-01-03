package io.musician101.morefish.forge.fishing.condition;

import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;

public final class ForgeEnchantmentCondition implements ForgeFishCondition {

    private final Enchantment enchantment;
    private final int minLevel;

    public ForgeEnchantmentCondition(@Nonnull Enchantment enchantment, int minLevel) {
        this.enchantment = enchantment;
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        ItemStack fishingRod = fisher.getHeldItem(Hand.MAIN_HAND);
        if (fishingRod.isEmpty()) {
            return false;
        }

        return fishingRod.getEnchantmentTagList().stream().map(CompoundNBT.class::cast).anyMatch(e -> e.getString("id").equals(enchantment.getRegistryName().toString()) && e.getInt("lvl") >= minLevel);
    }
}
