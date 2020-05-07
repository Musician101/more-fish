package io.musician101.morefish.sponge.fishing.condition;

import java.util.Collections;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongePotionEffectCondition implements SpongeFishCondition {

    private final PotionEffectType effectType;
    private final int minAmplifier;

    public SpongePotionEffectCondition(@Nonnull PotionEffectType effectType, int minAmplifier) {
        this.effectType = effectType;
        this.minAmplifier = minAmplifier;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return fisher.get(Keys.POTION_EFFECTS).orElse(Collections.emptyList()).stream().anyMatch(p -> p.getType() == effectType && p.getAmplifier() >= minAmplifier);
    }
}
