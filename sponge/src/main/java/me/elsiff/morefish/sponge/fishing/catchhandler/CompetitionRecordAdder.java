package me.elsiff.morefish.sponge.fishing.catchhandler;

import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.common.fishing.competition.FishRecord;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public record CompetitionRecordAdder() implements CatchHandler<SpongeFish, ServerPlayer> {

    public void handle(@NotNull ServerPlayer catcher, @NotNull SpongeFish fish) {
        SpongeFishingCompetition competition = getPlugin().getCompetition();
        if (competition.isEnabled()) {
            competition.putRecord(new FishRecord<>(catcher.uniqueId(), fish));
        }
    }
}
