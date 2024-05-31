package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.text.Lang;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CatchBroadcaster extends AbstractBroadcaster {

    @NotNull
    public String getCatchMessageFormat() {
        return Lang.raw("catch-message-format");
    }

    public boolean meetBroadcastCondition(@NotNull Player catcher, @NotNull Fish fish) {
        return !fish.type().catchAnnouncement().receiversOf(catcher).isEmpty();
    }
}
