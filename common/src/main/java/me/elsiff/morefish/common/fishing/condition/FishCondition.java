package me.elsiff.morefish.common.fishing.condition;

import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import org.jetbrains.annotations.NotNull;

public interface FishCondition<C extends FishingCompetition<?>, I, P> {

    boolean check(@NotNull I caught, @NotNull P fisher, @NotNull C fishingCompetition);
}
