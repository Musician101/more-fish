package me.elsiff.morefish.fishing.catchhandler;

import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.NoAnnouncement;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class CatchBroadcaster extends AbstractBroadcaster {

    @Nonnull
    public Component getCatchMessageFormat() {
        return Lang.CATCH_FISH;
    }

    public boolean meetBroadcastCondition(@Nonnull Player catcher, @Nonnull Fish fish) {
        return !(fish.type().catchAnnouncement() instanceof NoAnnouncement);
    }
}
