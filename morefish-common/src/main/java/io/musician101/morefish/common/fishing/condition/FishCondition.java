package io.musician101.morefish.common.fishing.condition;

import javax.annotation.Nonnull;

public interface FishCondition<I, P> {

    boolean check(@Nonnull I item, @Nonnull P player);
}
