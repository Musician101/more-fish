package me.elsiff.morefish.sponge.command;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

import static me.elsiff.morefish.common.configuration.Lang.ALREADY_STOPPED;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_STOP;

class MFEnd extends MFCommand {

    @Override
    public boolean canUse(@NotNull CommandContext context) {
        return testAdmin(context);
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
}
