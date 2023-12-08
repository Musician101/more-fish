package me.elsiff.morefish.paper.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.common.util.NumberUtils.Range;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record LocationYCondition(
        @NotNull Range<Double> range) implements FishCondition<PaperFishingCompetition, Item, Player> {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull PaperFishingCompetition fishingCompetition) {
        return range.containsDouble(fisher.getLocation().getY());
    }
}
