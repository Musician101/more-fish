package me.elsiff.morefish.fishing.condition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetition.State;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class CompetitionCondition implements FishCondition {

    private final State state;

    public CompetitionCondition(@Nonnull State state) {
        super();
        this.state = state;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        return fishingCompetition.getState() == this.state;
    }
}
