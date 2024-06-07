package me.elsiff.morefish.competition;

import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

public record Prize(@NotNull List<String> commands) {

    public void giveTo(@NotNull OfflinePlayer player, int rankNumber, @NotNull Plugin plugin) {
        String name = player.getName();
        if (!player.isOnline()) {
            plugin.getSLF4JLogger().warn("{} fisher {} isn't online! Contest prizes may not be sent.", NumberUtils.ordinalOf(rankNumber), name);
            return;
        }

        Server server = plugin.getServer();
        commands.forEach(command -> Bukkit.getGlobalRegionScheduler().run(getPlugin(), task -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", name == null ? "null" : name))));
    }
}
