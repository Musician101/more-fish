package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.catchhandler.CatchCommandExecutor;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public final class SpigotCatchCommandExecutor extends CatchCommandExecutor {

    public SpigotCatchCommandExecutor(@Nonnull List<String> commands) {
        super(commands);
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        Server server = Bukkit.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", Bukkit.getPlayer(catcherID).getName())));
    }
}
