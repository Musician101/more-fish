package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.text.Lang.raw;

public final class CatchBroadcaster extends AbstractBroadcaster {

    @NotNull
    public String getCatchMessageFormat() {
        return raw("catch-message-format");
    }

    public boolean meetBroadcastCondition(@NotNull Player catcher, @NotNull Fish fish) {
        return !fish.type().catchAnnouncement().receiversOf(catcher).isEmpty();
    }
}
