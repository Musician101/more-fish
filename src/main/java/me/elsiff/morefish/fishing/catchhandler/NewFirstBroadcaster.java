package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.text.Lang;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class NewFirstBroadcaster extends AbstractBroadcaster {

    @NotNull
    public String getCatchMessageFormat() {
        return Lang.raw("new-1st-message-format");
    }

    public boolean meetBroadcastCondition(@NotNull Player catcher, @NotNull Fish fish) {
        FishingCompetition competition = getPlugin().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher, fish);
    }
}
