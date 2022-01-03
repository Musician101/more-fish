package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SpongeNewFirstBroadcaster extends AbstractSpongeBroadcaster {

    @Nonnull
    public SpongeTextFormat getCatchMessageFormat() {
        return SpongeMoreFish.getInstance().getConfig().getLangConfig().format("get-1st");
    }

    public boolean meetBroadcastCondition(@Nonnull UUID catcher, @Nonnull Fish fish) {
        FishingCompetition competition = SpongeMoreFish.getInstance().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher, fish);
    }
}
