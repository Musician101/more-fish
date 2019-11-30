package me.elsiff.morefish.fishing.competition;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public final class Prize {

    private final List<String> commands;

    public Prize(@Nonnull List<String> commands) {
        super();
        this.commands = commands;
    }

    public final void giveTo(@Nonnull OfflinePlayer player, int rankNumber, @Nonnull Plugin plugin) {
        if (!player.isOnline()) {
            plugin.getLogger().warning(NumberUtils.ordinalOf(rankNumber) + " fisher " + player.getName() + " isn't online! Contest prizes may not be sent.");
        }

        Server server = plugin.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", player.getName())));
    }
}
