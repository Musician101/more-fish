package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.weather.WeatherTypes;

public final class SpongeThunderingCondition implements FishCondition {

    private final boolean thundering;

    public SpongeThunderingCondition(boolean thundering) {
        this.thundering = thundering;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return Sponge.server().worldManager().worlds().stream().anyMatch(world -> world.entity(caught).filter(entity -> thundering && world.weather().type() == WeatherTypes.THUNDER.get()).isPresent());
    }
}
