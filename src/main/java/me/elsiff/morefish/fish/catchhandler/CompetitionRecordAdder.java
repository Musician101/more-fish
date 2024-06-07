package me.elsiff.morefish.fish.catchhandler;

import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.competition.FishingCompetition;
import me.elsiff.morefish.records.FishRecord;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public record CompetitionRecordAdder() implements CatchHandler {

    public void handle(@NotNull Player catcher, @NotNull Fish fish) {
        FishingCompetition competition = getPlugin().getCompetition();
        if (competition.isEnabled()) {
            FishRecord record = new FishRecord(catcher.getUniqueId(), fish, System.currentTimeMillis());
            competition.add(record);
            getPlugin().getFishingLogs().add(record);
        }
    }
}
