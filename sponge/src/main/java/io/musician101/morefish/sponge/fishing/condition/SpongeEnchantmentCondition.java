package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;

public final class SpongeEnchantmentCondition implements FishCondition {

    private final EnchantmentType enchantment;
    private final int minLevel;

    public SpongeEnchantmentCondition(@Nonnull EnchantmentType enchantment, int minLevel) {
        this.enchantment = enchantment;
        this.minLevel = minLevel;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        ItemStack fishingRod = Sponge.server().player(fisher).get().itemInHand(HandTypes.MAIN_HAND);
        if (fishingRod.isEmpty()) {
            return false;
        }

        return fishingRod.get(Keys.APPLIED_ENCHANTMENTS).orElse(Collections.emptyList()).stream().anyMatch(e -> e.type() == enchantment && e.level() >= minLevel);
    }
}
