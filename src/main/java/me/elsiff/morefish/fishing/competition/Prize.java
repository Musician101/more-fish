package me.elsiff.morefish.fishing.competition;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public record Prize(@Nonnull List<String> commands) {

    public void giveTo(@Nonnull OfflinePlayer player, int rankNumber, @Nonnull Plugin plugin) {
        if (!player.isOnline()) {
            plugin.getLogger().warning(NumberUtils.ordinalOf(rankNumber) + " fisher " + player.getName() + " isn't online! Contest prizes may not be sent.");
            return;
        }

        Server server = plugin.getServer();
        String name = player.getName();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", name == null ? "null" : name)));
    }
}
