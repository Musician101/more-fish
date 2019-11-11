package me.elsiff.morefish.fishing.catchhandler;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.Record;
import org.bukkit.entity.Player;

public final class CompetitionRecordAdder implements CatchHandler {

    private final FishingCompetition competition;

    public CompetitionRecordAdder(@Nonnull FishingCompetition competition) {
        super();
        this.competition = competition;
    }

    public void handle(@Nonnull Player catcher, @Nonnull Fish fish) {
        if (this.competition.isEnabled()) {
            this.competition.putRecord(new Record(catcher, fish));
        }

    }
}
