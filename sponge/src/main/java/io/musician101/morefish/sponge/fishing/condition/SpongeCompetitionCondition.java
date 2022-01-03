package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.competition.FishingCompetition.State;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import io.musician101.morefish.sponge.SpongeMoreFish;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SpongeCompetitionCondition implements FishCondition {

    private final State state;

    public SpongeCompetitionCondition(@Nonnull State state) {
        this.state = state;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return SpongeMoreFish.getInstance().getCompetition().getState() == this.state;
    }
}
