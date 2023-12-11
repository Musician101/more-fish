package me.elsiff.morefish.sponge.command;

import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

class MFClear extends MFCommand {

    @Override
    public @NotNull Component description(CommandCause cause) {
        return text("Clear the fishing records.", GRAY);
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

    @Override
    public @NotNull Optional<String> getPermission() {
        return Optional.of("morefish.admin");
    }

    @Override
    public @NotNull Component usage(CommandCause cause) {
        return text("/mf clear");
    }
}
