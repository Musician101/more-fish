package me.elsiff.morefish.fishing.condition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record PotionEffectCondition(@Nonnull PotionEffectType effectType, int minAmplifier) implements FishCondition {

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        PotionEffect pe = fisher.getPotionEffect(effectType);
        return fisher.hasPotionEffect(effectType) && pe != null && pe.getAmplifier() >= minAmplifier;
    }
}
