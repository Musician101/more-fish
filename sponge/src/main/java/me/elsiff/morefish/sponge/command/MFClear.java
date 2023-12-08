package me.elsiff.morefish.sponge.command;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;

class MFClear extends MFCommand {

    @Override
    public boolean canUse(@NotNull CommandContext context) {
        return testAdmin(context);
    }

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        getCompetition().clearRecords();
        context.sendMessage(join(PREFIX, text("The records has been cleared successfully.")));
        return CommandResult.success();
    }

    @NotNull
    @Override
    public String getName() {
        return "clear";
    }
}
