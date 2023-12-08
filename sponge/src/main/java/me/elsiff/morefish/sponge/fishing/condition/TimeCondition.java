package me.elsiff.morefish.sponge.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.common.fishing.condition.TimeState;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public record TimeCondition(
        @NotNull TimeState state) implements FishCondition<SpongeFishingCompetition, Item, ServerPlayer> {

    public boolean check(@NotNull Item caught, @NotNull ServerPlayer fisher, @NotNull SpongeFishingCompetition fishingCompetition) {
        return TimeState.fromTime(caught.world().properties().dayTime().asTicks().ticks()).filter(timeState -> timeState == state).isPresent();
    }
}
