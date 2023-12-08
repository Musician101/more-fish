package me.elsiff.morefish.paper.fishing.catchhandler;

import java.util.List;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.paper.fishing.PaperFish;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record CatchCommandExecutor(@NotNull List<String> commands) implements CatchHandler<PaperFish, Player> {

    @Override
    public void handle(@NotNull Player catcher, @NotNull PaperFish fish) {
        Server server = Bukkit.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", catcher.getName())));
    }
}
