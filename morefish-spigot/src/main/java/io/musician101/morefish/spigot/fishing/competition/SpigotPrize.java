package io.musician101.morefish.spigot.fishing.competition;

import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.util.NumberUtils;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public final class SpigotPrize extends Prize<OfflinePlayer> {

    public SpigotPrize(@Nonnull List<String> commands) {
        super(commands);
    }

    @Override
    public void giveTo(@Nonnull OfflinePlayer user, int rankNumber) {
        if (!user.isOnline()) {
            SpigotMoreFish.getInstance().getLogger().warning(NumberUtils.ordinalOf(rankNumber) + " fisher " + user.getName() + " isn't online! Contest prizes may not be sent.");
        }

        Server server = Bukkit.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", user.getName())));
    }
}
