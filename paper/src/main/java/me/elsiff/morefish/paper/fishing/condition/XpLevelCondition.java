package me.elsiff.morefish.paper.fishing.condition;

import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record XpLevelCondition(int minLevel) implements FishCondition<PaperFishingCompetition, Item, Player> {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull PaperFishingCompetition fishingCompetition) {
        return fisher.getLevel() >= this.minLevel;
    }
}
