package me.elsiff.morefish.fishing.catchhandler;

import java.util.List;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record CatchCommandExecutor(@NotNull List<String> commands) implements CatchHandler {

    public void handle(@NotNull Player catcher, @NotNull Fish fish) {
        Server server = catcher.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", catcher.getName())));
    }
}
