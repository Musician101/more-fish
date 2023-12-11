package me.elsiff.morefish.paper.fishing.catchhandler;

import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.common.fishing.competition.FishRecord;
import me.elsiff.morefish.paper.fishing.PaperFish;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public record CompetitionRecordAdder() implements CatchHandler<PaperFish, Player> {

    public void handle(@NotNull Player catcher, @NotNull PaperFish fish) {
        PaperFishingCompetition competition = getPlugin().getCompetition();
        if (competition.isEnabled()) {
            competition.putRecord(new FishRecord<>(catcher.getUniqueId(), fish));
        }
    }
}
