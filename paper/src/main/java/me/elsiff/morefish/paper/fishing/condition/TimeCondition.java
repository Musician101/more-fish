package me.elsiff.morefish.paper.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.common.fishing.condition.TimeState;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record TimeCondition(@NotNull TimeState state) implements FishCondition<PaperFishingCompetition, Item, Player> {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull PaperFishingCompetition fishingCompetition) {
        return TimeState.fromTime(caught.getWorld().getTime()).filter(timeState -> timeState == state).isPresent();
    }
}
