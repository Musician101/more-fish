package me.elsiff.morefish.fishing.condition;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record PotionEffectCondition(@NotNull Map<PotionEffectType, Integer> potionEffects) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher) {
        return potionEffects.entrySet().stream().allMatch(e -> {
            PotionEffect pe = fisher.getPotionEffect(e.getKey());
            return fisher.hasPotionEffect(e.getKey()) && pe != null && pe.getAmplifier() >= e.getValue();
        });
    }
}
