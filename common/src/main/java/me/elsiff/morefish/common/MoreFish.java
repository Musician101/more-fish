package me.elsiff.morefish.common;

import me.elsiff.morefish.common.fishing.FishBags;
import me.elsiff.morefish.common.fishing.FishTypeTable;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import me.elsiff.morefish.common.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.common.shop.FishShop;
import org.jetbrains.annotations.NotNull;

public interface MoreFish<B extends FishBags<?, ?>, C extends FishingCompetition<?>, H extends FishingCompetitionHost<?, ?, ?>, T extends FishTypeTable<C, ?, ?, ?, ?, ?>> {

    void applyConfig();

    @NotNull C getCompetition();

    @NotNull H getCompetitionHost();

    @NotNull B getFishBags();

    @NotNull FishShop getFishShop();

    @NotNull T getFishTypeTable();
}
