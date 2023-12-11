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

public class MFSuspend extends MFCommand {

    @Override
    public @NotNull Component description(CommandCause cause) {
        return text("Suspend a competition.", GRAY);
    }

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        if (!getCompetition().isDisabled()) {
            getCompetitionHost().closeCompetition(true);
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
        return "suspend";
    }

    @Override
    public @NotNull Optional<String> getPermission() {
        return Optional.of("morefish.admin");
    }

    @Override
    public @NotNull Component usage(CommandCause commandCause) {
        return text("/mf suspend");
    }
}
