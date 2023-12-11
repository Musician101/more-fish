package me.elsiff.morefish.sponge.command;

import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

import static me.elsiff.morefish.common.configuration.Lang.ALREADY_STOPPED;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_STOP;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

class MFEnd extends MFCommand {

    @Override
    public @NotNull Component description(CommandCause cause) {
        return text("Stops the fishing competition.", GRAY);
    }

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        if (getCompetition().isEnabled()) {
            getCompetitionHost().closeCompetition();
            if (!getConfig().node("messages.broadcast-stop").getBoolean(false)) {
                context.sendMessage(CONTEST_STOP);
            }
        }
        else {
            context.sendMessage(ALREADY_STOPPED);
        }

        return CommandResult.success();
    }

    @NotNull
    @Override
    public String getName() {
        return "end";
    }

    @Override
    public @NotNull Optional<String> getPermission() {
        return Optional.of("morefish.admin");
    }

    @Override
    public @NotNull Component usage(CommandCause cause) {
        return text("/mf end");
    }
}
