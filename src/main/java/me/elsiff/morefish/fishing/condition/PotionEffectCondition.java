package me.elsiff.morefish.fishing.condition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public final class PotionEffectCondition implements FishCondition {

    private final PotionEffectType effectType;
    private final int minAmplifier;

    public PotionEffectCondition(@Nonnull PotionEffectType effectType, int minAmplifier) {
        super();
        this.effectType = effectType;
        this.minAmplifier = minAmplifier;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        return fisher.hasPotionEffect(effectType) && fisher.getPotionEffect(effectType).getAmplifier() >= minAmplifier;
    }
}
