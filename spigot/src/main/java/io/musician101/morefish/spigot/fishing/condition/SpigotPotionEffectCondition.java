package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record SpigotPotionEffectCondition(PotionEffectType effectType, int minAmplifier) implements FishCondition {

    @SuppressWarnings("ConstantConditions")
    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        PotionEffect potionEffect = Bukkit.getPlayer(fisher).getPotionEffect(effectType);
        return potionEffect != null && potionEffect.getAmplifier() >= minAmplifier;
    }
}
