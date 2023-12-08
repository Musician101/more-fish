package me.elsiff.morefish.sponge.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public record PotionEffectCondition(@NotNull PotionEffectType effectType,
                                    int minAmplifier) implements FishCondition<SpongeFishingCompetition, Item, ServerPlayer> {

    public boolean check(@NotNull Item caught, @NotNull ServerPlayer fisher, @NotNull SpongeFishingCompetition fishingCompetition) {
        return fisher.potionEffects().all().stream().anyMatch(pe -> pe.type().equals(effectType) && pe.amplifier() >= minAmplifier);
    }
}
