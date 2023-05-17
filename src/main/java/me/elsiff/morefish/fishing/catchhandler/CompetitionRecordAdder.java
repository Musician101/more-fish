package me.elsiff.morefish.fishing.catchhandler;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.Record;
import org.bukkit.entity.Player;

import static me.elsiff.morefish.MoreFish.getPlugin;

public record CompetitionRecordAdder() implements CatchHandler {

    public void handle(@Nonnull Player catcher, @Nonnull Fish fish) {
        FishingCompetition competition = getPlugin().getCompetition();
        if (competition.isEnabled()) {
            competition.putRecord(new Record(catcher.getUniqueId(), fish));
        }

    }
}
