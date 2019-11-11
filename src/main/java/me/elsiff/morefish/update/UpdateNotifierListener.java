package me.elsiff.morefish.update;

import com.google.common.collect.ImmutableMap;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class UpdateNotifierListener implements Listener {

    private final String newVersion;

    public UpdateNotifierListener(@Nonnull String newVersion) {
        super();
        this.newVersion = newVersion;
    }

    @EventHandler
    public final void onPlayerJoin(@Nonnull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            player.sendMessage(Lang.INSTANCE.formats("new-version").replace(ImmutableMap.of("%s", newVersion)).output(player).toArray(new String[0]));
        }
    }
}
