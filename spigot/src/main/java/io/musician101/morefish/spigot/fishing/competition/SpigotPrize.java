package io.musician101.morefish.spigot.fishing.competition;

import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.util.NumberUtils;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public final class SpigotPrize extends Prize {

    public SpigotPrize(@Nonnull List<String> commands) {
        super(commands);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void giveTo(@Nonnull UUID user, int rankNumber) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user);
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            SpigotMoreFish.getInstance().getLogger().warning(NumberUtils.ordinalOf(rankNumber) + " fisher " + offlinePlayer.getName() + " isn't online! Contest prizes may not be sent.");
        }

        Server server = Bukkit.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", offlinePlayer.getName())));
    }
}
