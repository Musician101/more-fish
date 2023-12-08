package me.elsiff.morefish.common.fishing.catchhandler;

import me.elsiff.morefish.common.fishing.Fish;
import me.elsiff.morefish.common.fishing.FishType;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface AbstractBroadcaster<C extends FishingCompetition<F>, F extends Fish<?>, P> extends CatchHandler<F, P> {

    default <T extends FishType<C, F, ?, ?, ?>> String fishNameWithRarity(T fishType) {
        String s = fishType.displayName();
        if (fishType.noDisplay()) {
            return s;
        }

        return fishType.rarity().displayName().toUpperCase() + " " + s;
    }

    @NotNull Component getCatchMessageFormat();

    boolean meetBroadcastCondition(@NotNull P catcher, @NotNull F fish);
}
