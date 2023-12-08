package me.elsiff.morefish.sponge.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;

public record EnchantmentCondition(@NotNull EnchantmentType enchantment,
                                   int minLevel) implements FishCondition<SpongeFishingCompetition, Item, ServerPlayer> {

    public boolean check(@NotNull Item caught, @NotNull ServerPlayer fisher, @NotNull SpongeFishingCompetition fishingCompetition) {
        ItemStack fishingRod = fisher.itemInHand(HandTypes.MAIN_HAND);
        if (fishingRod.type().equals(ItemTypes.AIR.get())) {
            return false;
        }

        return fishingRod.get(Keys.APPLIED_ENCHANTMENTS).map(enchants -> enchants.stream().anyMatch(e -> e.type().equals(enchantment) && e.level() >= minLevel)).isPresent();
    }
}
