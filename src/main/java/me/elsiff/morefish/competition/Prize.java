package me.elsiff.morefish.competition;

import me.elsiff.morefish.lang.ArgumentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public record Prize(List<String> commands) {

    public void giveTo(OfflinePlayer player, Plugin plugin) {
        String name = player.getName();
        if (!player.isOnline()) {
            plugin.getComponentLogger().warn(Component.translatable("morefish.main.prize-warning", ArgumentUtil.player(player)));
            return;
        }

        Server server = plugin.getServer();
        commands.forEach(command -> Bukkit.getGlobalRegionScheduler().run(getPlugin(), task -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", name == null ? "null" : name))));
    }
}
