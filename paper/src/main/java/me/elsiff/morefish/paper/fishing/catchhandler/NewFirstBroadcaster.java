package me.elsiff.morefish.paper.fishing.catchhandler;

import me.elsiff.morefish.common.configuration.Lang;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import me.elsiff.morefish.paper.fishing.PaperFish;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public final class NewFirstBroadcaster implements PaperBroadcaster {

    @NotNull
    public Component getCatchMessageFormat() {
        return Lang.join(Lang.PREFIX, text("%player%", YELLOW), text(" is now 1st!"));
    }

    @Override
    public boolean meetBroadcastCondition(@NotNull Player catcher, @NotNull PaperFish fish) {
        PaperFishingCompetition competition = getPlugin().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher.getUniqueId(), fish);
    }
}
