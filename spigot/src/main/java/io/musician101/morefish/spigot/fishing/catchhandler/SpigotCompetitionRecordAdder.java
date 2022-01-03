package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.spigot.SpigotMoreFish;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SpigotCompetitionRecordAdder implements CatchHandler {

    public void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        FishingCompetition competition = SpigotMoreFish.getInstance().getCompetition();
        if (competition.isEnabled()) {
            competition.putRecord(new Record(catcherID, fish));
        }
    }
}
