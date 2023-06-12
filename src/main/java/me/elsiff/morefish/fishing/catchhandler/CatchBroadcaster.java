package me.elsiff.morefish.fishing.catchhandler;

import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.NoAnnouncement;
import me.elsiff.morefish.fishing.Fish;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Player;

import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public final class CatchBroadcaster extends AbstractBroadcaster {

    @Nonnull
    public Component getCatchMessageFormat() {
        return join(PREFIX, text("%player%", YELLOW), text(" caught "), text("%rarity_color%%length%cm "), text("%rarity_color%%fish_with_rarity%", Style.style(BOLD)));
    }

    public boolean meetBroadcastCondition(@Nonnull Player catcher, @Nonnull Fish fish) {
        return !(fish.type().catchAnnouncement() instanceof NoAnnouncement);
    }
}
