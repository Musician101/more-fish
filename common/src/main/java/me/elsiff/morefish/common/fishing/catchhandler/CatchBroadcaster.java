package me.elsiff.morefish.common.fishing.catchhandler;

import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.common.fishing.Fish;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.common.configuration.Lang.PREFIX;
import static me.elsiff.morefish.common.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public interface CatchBroadcaster<C extends FishingCompetition<F>, F extends Fish<?>, P> extends AbstractBroadcaster<C, F, P> {

    @NotNull
    default Component getCatchMessageFormat() {
        return join(PREFIX, text("%player%", YELLOW), text(" caught "), text("%rarity_color%%length%cm "), text("%rarity_color%%fish_with_rarity%", Style.style(BOLD)));
    }

    default boolean meetBroadcastCondition(@NotNull P catcher, @NotNull F fish) {
        return !(fish.type().catchAnnouncement() instanceof PlayerAnnouncement.NoAnnouncement);
    }
}
