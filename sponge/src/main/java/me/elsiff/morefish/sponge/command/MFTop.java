package me.elsiff.morefish.sponge.command;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

public class MFTop extends MFCommand {

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        getCompetitionHost().informAboutRanking(context.cause().audience());
        return CommandResult.success();
    }

    @NotNull
    @Override
    public String getName() {
        return "top";
    }
}
