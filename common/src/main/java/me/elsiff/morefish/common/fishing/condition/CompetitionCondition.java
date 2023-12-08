package me.elsiff.morefish.common.fishing.condition;

import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import org.jetbrains.annotations.NotNull;

public record CompetitionCondition<C extends FishingCompetition<?>, I, P>(
        @NotNull FishingCompetition.State state) implements FishCondition<C, I, P> {

    public boolean check(@NotNull I caught, @NotNull P fisher, @NotNull C fishingCompetition) {
        return fishingCompetition.getState() == this.state;
    }
}
