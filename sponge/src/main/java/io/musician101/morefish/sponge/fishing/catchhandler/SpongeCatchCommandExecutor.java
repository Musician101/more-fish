package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.catchhandler.CatchCommandExecutor;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public final class SpongeCatchCommandExecutor extends CatchCommandExecutor {

    public SpongeCatchCommandExecutor(@Nonnull List<String> commands) {
        super(commands);
    }

    public void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        commands.forEach(command -> {
            Server server = Sponge.server();
            server.player(catcherID).map(ServerPlayer::name).ifPresent(name -> {
                try {
                    server.commandManager().process(command.replace("@p", name));
                }
                catch (CommandException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
