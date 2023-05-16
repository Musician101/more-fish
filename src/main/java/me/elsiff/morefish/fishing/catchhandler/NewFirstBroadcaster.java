package me.elsiff.morefish.fishing.catchhandler;

import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class NewFirstBroadcaster extends AbstractBroadcaster {

    @Nonnull
    public Component getCatchMessageFormat() {
        return Lang.GET_1ST;
    }

    public boolean meetBroadcastCondition(@Nonnull Player catcher, @Nonnull Fish fish) {
        FishingCompetition competition = MoreFish.instance().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher, fish);
    }
}
