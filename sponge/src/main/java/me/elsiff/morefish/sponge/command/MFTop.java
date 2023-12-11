package me.elsiff.morefish.sponge.command;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

import static net.kyori.adventure.text.Component.text;

public class MFTop extends MFCommand {

    @Override
    public @NotNull Component description(CommandCause cause) {
        return text("View the top 3 players.");
    }

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

    @Override
    public @NotNull Component usage(CommandCause commandCause) {
        return text("/mf top");
    }
}
