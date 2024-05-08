package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.text.Lang.PREFIX_STRING;

public final class CatchBroadcaster extends AbstractBroadcaster {

    @NotNull
    public String getCatchMessageFormat() {
        return PREFIX_STRING + "<yellow>%player% <white>caught <color:%rarity_color%>%length%cm <bold><color:%rarity_color%>%fish_with_rarity%";
    }

    public boolean meetBroadcastCondition(@NotNull Player catcher, @NotNull Fish fish) {
        return !fish.type().catchAnnouncement().receiversOf(catcher).isEmpty();
    }
}
