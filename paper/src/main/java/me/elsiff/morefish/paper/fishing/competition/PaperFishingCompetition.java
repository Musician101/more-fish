package me.elsiff.morefish.paper.fishing.competition;

import me.elsiff.morefish.common.fishing.competition.FishRecord;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import me.elsiff.morefish.paper.fishing.PaperFish;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public final class PaperFishingCompetition extends FishingCompetition<PaperFish> {

    @Override
    public void disable() {
        getPlugin().getMusiBoard().clear();
    }

    @Override
    public void putRecord(@NotNull FishRecord<PaperFish> record) {
        super.putRecord(record);
        getPlugin().getMusiBoard().update();
    }
}
