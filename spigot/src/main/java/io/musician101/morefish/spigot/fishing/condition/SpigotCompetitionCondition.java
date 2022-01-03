package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.competition.FishingCompetition.State;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import io.musician101.morefish.spigot.SpigotMoreFish;
import java.util.UUID;
import javax.annotation.Nonnull;

public record SpigotCompetitionCondition(State state) implements FishCondition {

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return SpigotMoreFish.getInstance().getCompetition().getState() == this.state;
    }
}
