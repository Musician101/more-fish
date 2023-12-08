package me.elsiff.morefish.fishing.condition;

import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public record PotionEffectCondition(@NotNull PotionEffectType effectType, int minAmplifier) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull FishingCompetition fishingCompetition) {
        PotionEffect pe = fisher.getPotionEffect(effectType);
        return fisher.hasPotionEffect(effectType) && pe != null && pe.getAmplifier() >= minAmplifier;
    }
}
