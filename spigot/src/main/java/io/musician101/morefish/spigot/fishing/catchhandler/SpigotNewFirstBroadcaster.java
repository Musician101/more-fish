package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SpigotNewFirstBroadcaster extends AbstractSpigotBroadcaster {

    @Nonnull
    @Override
    public SpigotTextFormat getCatchMessageFormat() {
        return SpigotMoreFish.getInstance().getPluginConfig().getLangConfig().format("get-1st");
    }

    @Override
    public boolean meetBroadcastCondition(@Nonnull UUID catcher, @Nonnull Fish fish) {
        FishingCompetition competition = SpigotMoreFish.getInstance().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher, fish);
    }
}
