package me.elsiff.morefish.sponge.fishing.catchhandler;

import me.elsiff.morefish.sponge.fishing.SpongeFish;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public final class NewFirstBroadcaster implements SpongeBroadcaster {

    @NotNull
    public Component getCatchMessageFormat() {
        return join(PREFIX, text("%player%", YELLOW), text(" is now 1st!"));
    }

    @Override
    public boolean meetBroadcastCondition(@NotNull ServerPlayer catcher, @NotNull SpongeFish fish) {
        SpongeFishingCompetition competition = getPlugin().getCompetition();
        return competition.isEnabled() && competition.willBeNewFirst(catcher.uniqueId(), fish);
    }
}
