package me.elsiff.morefish.sponge.fishing.catchhandler;

import me.elsiff.morefish.common.fishing.catchhandler.CatchBroadcaster;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public final class SpongeCatchBroadcaster implements CatchBroadcaster<SpongeFishingCompetition, SpongeFish, ServerPlayer>, SpongeBroadcaster {

    @NotNull
    public Component getCatchMessageFormat() {
        return join(PREFIX, text("%player%", YELLOW), text(" caught "), text("%rarity_color%%length%cm "), text("%rarity_color%%fish_with_rarity%", Style.style(BOLD)));
    }
}
