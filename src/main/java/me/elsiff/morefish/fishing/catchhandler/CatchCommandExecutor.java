package me.elsiff.morefish.fishing.catchhandler;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public record CatchCommandExecutor(@Nonnull List<String> commands) implements CatchHandler {

    public void handle(@Nonnull Player catcher, @Nonnull Fish fish) {
        Server server = catcher.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", catcher.getName())));
    }
}
