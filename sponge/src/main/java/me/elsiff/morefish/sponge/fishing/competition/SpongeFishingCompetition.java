package me.elsiff.morefish.sponge.fishing.competition;

import me.elsiff.morefish.common.fishing.competition.FishRecord;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public final class SpongeFishingCompetition extends FishingCompetition<SpongeFish> {

    @Override
    public void disable() {
        getPlugin().getScoreboardHooker().clear();
    }

    @Override
    public void putRecord(@NotNull FishRecord<SpongeFish> record) {
        super.putRecord(record);
        getPlugin().getScoreboardHooker().update();
    }
}
