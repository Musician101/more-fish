package me.elsiff.morefish.fishing.condition;

import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetition.State;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record CompetitionCondition(@NotNull State state) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull FishingCompetition fishingCompetition) {
        return fishingCompetition.getState() == this.state;
    }
}
