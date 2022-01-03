package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.weather.WeatherTypes;

public final class SpongeRainingCondition implements FishCondition {

    private final boolean raining;

    public SpongeRainingCondition(boolean raining) {
        this.raining = raining;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return Sponge.server().worldManager().worlds().stream().anyMatch(world -> world.entity(caught).filter(entity -> raining && world.weather().type() == WeatherTypes.RAIN.get()).isPresent());
    }
}
