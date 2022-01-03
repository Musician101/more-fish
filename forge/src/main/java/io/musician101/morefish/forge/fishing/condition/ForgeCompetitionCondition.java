package io.musician101.morefish.forge.fishing.condition;

import io.musician101.morefish.common.fishing.competition.FishingCompetition.State;
import io.musician101.morefish.forge.ForgeMoreFish;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class ForgeCompetitionCondition implements ForgeFishCondition {

    private final State state;

    public ForgeCompetitionCondition(@Nonnull State state) {
        this.state = state;
    }

    public boolean check(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return ForgeMoreFish.getInstance().getCompetition().getState() == this.state;
    }
}
