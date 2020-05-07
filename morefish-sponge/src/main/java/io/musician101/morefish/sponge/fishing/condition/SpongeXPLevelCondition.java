package io.musician101.morefish.sponge.fishing.condition;

import javax.annotation.Nonnull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongeXPLevelCondition implements SpongeFishCondition {

    private final int minLevel;

    public SpongeXPLevelCondition(int minLevel) {
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return fisher.get(Keys.EXPERIENCE_LEVEL).filter(level -> level >= minLevel).isPresent();
    }
}
