package me.elsiff.morefish.fishing.competition;

import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Prize(@NotNull List<String> commands) {

    public void giveTo(@NotNull OfflinePlayer player, int rankNumber, @NotNull Plugin plugin) {
        if (!player.isOnline()) {
            plugin.getLogger().warning(NumberUtils.ordinalOf(rankNumber) + " fisher " + player.getName() + " isn't online! Contest prizes may not be sent.");
            return;
        }

        Server server = plugin.getServer();
        String name = player.getName();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", name == null ? "null" : name)));
    }
}
