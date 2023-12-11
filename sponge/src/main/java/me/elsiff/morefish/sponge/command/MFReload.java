package me.elsiff.morefish.sponge.command;

import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class MFReload extends MFCommand {

    @Override
    public @NotNull Component description(CommandCause cause) {
        return text("Reload the config from disk.", GRAY);
    }

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        try {
            getPlugin().applyConfig();
            context.sendMessage(join(PREFIX, text("Reloaded the config successfully.")));
        }
        catch (Exception e) {
            getPlugin().getLogger().error("An error occurred while reloading the config.", e);
            context.sendMessage(join(PREFIX, text("Failed to reload: Please check your console.")));
        }

        return CommandResult.success();
    }

    @NotNull
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public @NotNull Optional<String> getPermission() {
        return Optional.of("morefish.admin");
    }

    @Override
    public @NotNull Component usage(CommandCause commandCause) {
        return text("/mf reload");
    }
}
