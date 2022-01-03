package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.potion.PotionEffectType;

public final class SpongePotionEffectCondition implements FishCondition {

    private final PotionEffectType effectType;
    private final int minAmplifier;

    public SpongePotionEffectCondition(@Nonnull PotionEffectType effectType, int minAmplifier) {
        this.effectType = effectType;
        this.minAmplifier = minAmplifier;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return Sponge.server().player(fisher).flatMap(player -> player.get(Keys.POTION_EFFECTS)).orElse(Collections.emptyList()).stream().anyMatch(p -> p.type() == effectType && p.amplifier() >= minAmplifier);
    }
}
