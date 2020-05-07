package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SpigotCatchCommandExecutor implements SpigotCatchHandler {

    private final List<String> commands;

    public SpigotCatchCommandExecutor(@Nonnull List<String> commands) {
        this.commands = commands;
    }

    public void handle(@Nonnull Player catcher, @Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        Server server = catcher.getServer();
        commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", catcher.getName())));
    }
}
