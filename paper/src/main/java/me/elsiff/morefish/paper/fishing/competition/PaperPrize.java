package me.elsiff.morefish.paper.fishing.competition;

import java.util.List;
import java.util.UUID;
import me.elsiff.morefish.common.fishing.competition.Prize;
import me.elsiff.morefish.common.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public final class PaperPrize extends Prize {

    public PaperPrize(@NotNull List<String> commands) {
        super(commands);
    }

    public void giveTo(@NotNull UUID player, int rankNumber) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        if (!offlinePlayer.isOnline()) {
            getPlugin().getLogger().warning(NumberUtils.ordinalOf(rankNumber) + " fisher " + offlinePlayer.getName() + " isn't online! Contest prizes may not be sent.");
            return;
        }

        Server server = getPlugin().getServer();
        String name = offlinePlayer.getName();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", name == null ? "null" : name)));
    }
}
