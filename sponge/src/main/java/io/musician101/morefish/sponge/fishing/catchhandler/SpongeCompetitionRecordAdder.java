package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.sponge.SpongeMoreFish;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SpongeCompetitionRecordAdder implements CatchHandler {

    public void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        FishingCompetition competition = SpongeMoreFish.getInstance().getCompetition();
        if (competition.isEnabled()) {
            competition.putRecord(new Record(catcherID, fish));
        }

    }
}
