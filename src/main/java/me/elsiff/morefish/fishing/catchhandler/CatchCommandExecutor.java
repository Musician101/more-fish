package me.elsiff.morefish.fishing.catchhandler;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public final class CatchCommandExecutor implements CatchHandler {

    private final List<String> commands;

    public CatchCommandExecutor(@Nonnull List<String> commands) {
        super();
        this.commands = commands;
    }

    public void handle(@Nonnull Player catcher, @Nonnull Fish fish) {
        Server server = catcher.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", catcher.getName())));
    }
}
