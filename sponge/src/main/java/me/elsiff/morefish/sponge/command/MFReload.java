package me.elsiff.morefish.sponge.command;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;

public class MFReload extends MFCommand {

    @Override
    public boolean canUse(@NotNull CommandContext context) {
        return testAdmin(context);
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
}
