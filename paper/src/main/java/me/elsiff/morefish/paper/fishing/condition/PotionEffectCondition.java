package me.elsiff.morefish.paper.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public record PotionEffectCondition(@NotNull PotionEffectType effectType,
                                    int minAmplifier) implements FishCondition<PaperFishingCompetition, Item, Player> {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull PaperFishingCompetition fishingCompetition) {
        PotionEffect pe = fisher.getPotionEffect(effectType);
        return fisher.hasPotionEffect(effectType) && pe != null && pe.getAmplifier() >= minAmplifier;
    }
}
