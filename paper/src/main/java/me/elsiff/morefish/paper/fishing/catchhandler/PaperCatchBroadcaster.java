package me.elsiff.morefish.paper.fishing.catchhandler;

import me.elsiff.morefish.common.fishing.catchhandler.CatchBroadcaster;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import me.elsiff.morefish.paper.fishing.PaperFish;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.configuration.PaperLang.PREFIX;
import static me.elsiff.morefish.paper.configuration.PaperLang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public final class PaperCatchBroadcaster implements CatchBroadcaster<PaperFishingCompetition, PaperFish, Player>, PaperBroadcaster {

    @NotNull
    public Component getCatchMessageFormat() {
        return join(PREFIX, text("%player%", YELLOW), text(" caught "), text("%rarity_color%%length%cm "), text("%rarity_color%%fish_with_rarity%", Style.style(BOLD)));
    }
}
