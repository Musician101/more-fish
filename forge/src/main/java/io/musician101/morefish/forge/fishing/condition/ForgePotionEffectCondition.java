package io.musician101.morefish.forge.fishing.condition;

import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public final class ForgePotionEffectCondition implements ForgeFishCondition {

    private final Effect effectType;
    private final int minAmplifier;

    public ForgePotionEffectCondition(@Nonnull Effect effectType, int minAmplifier) {
        this.effectType = effectType;
        this.minAmplifier = minAmplifier;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        EffectInstance effectInstance = fisher.getActivePotionEffect(effectType);
        return effectInstance != null && effectInstance.getAmplifier() >= minAmplifier;
    }
}
