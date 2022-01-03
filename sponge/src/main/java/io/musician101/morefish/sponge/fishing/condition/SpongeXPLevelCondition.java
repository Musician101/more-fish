package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;

public final class SpongeXPLevelCondition implements FishCondition {

    private final int minLevel;

    public SpongeXPLevelCondition(int minLevel) {
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return Sponge.server().player(fisher).flatMap(player -> player.get(Keys.EXPERIENCE_LEVEL)).filter(level -> level >= minLevel).isPresent();
    }
}
