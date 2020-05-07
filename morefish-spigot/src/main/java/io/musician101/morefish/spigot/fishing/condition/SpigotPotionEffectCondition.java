package io.musician101.morefish.spigot.fishing.condition;

import javax.annotation.Nonnull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public final class SpigotPotionEffectCondition implements SpigotFishCondition {

    private final PotionEffectType effectType;
    private final int minAmplifier;

    public SpigotPotionEffectCondition(@Nonnull PotionEffectType effectType, int minAmplifier) {
        this.effectType = effectType;
        this.minAmplifier = minAmplifier;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return fisher.hasPotionEffect(effectType) && fisher.getPotionEffect(effectType).getAmplifier() >= minAmplifier;
    }
}
