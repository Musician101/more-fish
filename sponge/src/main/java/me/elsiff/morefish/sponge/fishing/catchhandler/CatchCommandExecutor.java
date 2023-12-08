package me.elsiff.morefish.sponge.fishing.catchhandler;

import java.util.List;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import static me.elsiff.morefish.common.configuration.Lang.PREFIX;
import static me.elsiff.morefish.common.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public record CatchCommandExecutor(@NotNull List<String> commands) implements CatchHandler<SpongeFish, ServerPlayer> {

    @Override
    public void handle(@NotNull ServerPlayer catcher, @NotNull SpongeFish fish) {
        commands.forEach(command -> {
            try {
                Sponge.server().commandManager().process(command.replace("@p", catcher.name()));
            }
            catch (CommandException e) {
                catcher.sendMessage(join(PREFIX, text("Seems the fish you caught has a broken catch handler. Please contact server staff.", RED)));
            }
        });
    }
}
