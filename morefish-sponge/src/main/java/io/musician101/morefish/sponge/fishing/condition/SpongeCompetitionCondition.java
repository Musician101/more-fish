package io.musician101.morefish.sponge.fishing.condition;

import io.musician101.morefish.common.fishing.competition.FishingCompetition.State;
import io.musician101.morefish.sponge.SpongeMoreFish;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;

public final class SpongeCompetitionCondition implements SpongeFishCondition {

    private final State state;

    public SpongeCompetitionCondition(@Nonnull State state) {
        this.state = state;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return SpongeMoreFish.getInstance().getCompetition().getState() == this.state;
    }
}
