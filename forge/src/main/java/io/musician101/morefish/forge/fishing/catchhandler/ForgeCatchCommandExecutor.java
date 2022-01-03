package io.musician101.morefish.forge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.server.MinecraftServer;

public final class ForgeCatchCommandExecutor implements ForgeCatchHandler {

    private final List<String> commands;

    public ForgeCatchCommandExecutor(@Nonnull List<String> commands) {
        this.commands = commands;
    }

    public void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        MinecraftServer server = catcherID.getServer();
        commands.forEach(command -> server.getCommandManager().handleCommand(server.getCommandSource(), command.replace("@p", catcherID.getName().getString())));
    }
}
