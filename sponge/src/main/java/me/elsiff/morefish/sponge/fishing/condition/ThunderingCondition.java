package me.elsiff.morefish.sponge.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.weather.WeatherTypes;

public record ThunderingCondition(
        boolean thundering) implements FishCondition<SpongeFishingCompetition, Item, ServerPlayer> {

    public boolean check(@NotNull Item caught, @NotNull ServerPlayer fisher, @NotNull SpongeFishingCompetition fishingCompetition) {
        return caught.world().weather().type().equals(WeatherTypes.THUNDER.get()) == thundering;
    }
}
