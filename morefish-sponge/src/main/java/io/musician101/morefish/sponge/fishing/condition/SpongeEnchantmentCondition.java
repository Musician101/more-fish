package io.musician101.morefish.sponge.fishing.condition;

import java.util.Collections;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;

public final class SpongeEnchantmentCondition implements SpongeFishCondition {

    private final EnchantmentType enchantment;
    private final int minLevel;

    public SpongeEnchantmentCondition(@Nonnull EnchantmentType enchantment, int minLevel) {
        this.enchantment = enchantment;
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        ItemStack fishingRod = fisher.getItemInHand(HandTypes.MAIN_HAND).orElse(ItemStack.empty());
        if (fishingRod.isEmpty()) {
            return false;
        }

        return fishingRod.get(Keys.ITEM_ENCHANTMENTS).orElse(Collections.emptyList()).stream().anyMatch(e -> e.getType() == enchantment && e.getLevel() >= minLevel);
    }
}
