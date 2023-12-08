package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public final class NewFirstBroadcaster extends AbstractBroadcaster {

    @NotNull
    public Component getCatchMessageFormat() {
        return join(PREFIX, text("%player%", YELLOW), text(" is now 1st!"));
    }

    public boolean meetBroadcastCondition(@NotNull Player catcher, @NotNull Fish fish) {
        FishingCompetition competition = getPlugin().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher, fish);
    }
}
