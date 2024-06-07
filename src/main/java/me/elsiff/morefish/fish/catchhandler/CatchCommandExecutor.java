package me.elsiff.morefish.fish.catchhandler;

import me.elsiff.morefish.fish.Fish;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CatchCommandExecutor(@NotNull List<String> commands) implements CatchHandler {

    public void handle(@NotNull Player catcher, @NotNull Fish fish) {
        Server server = catcher.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", catcher.getName())));
    }
}
