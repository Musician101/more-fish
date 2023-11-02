package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.MoreFish;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;

public class MFReload extends MFCommand implements LiteralCommand {

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return testAdmin(sender);
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        try {
            getPlugin().applyConfig();
            sender.sendMessage(join(PREFIX, text("Reloaded the config successfully.")));
        }
        catch (Exception e) {
            MoreFish.getPlugin().getSLF4JLogger().error("An error occurred while reloading the config.", e);
            sender.sendMessage(join(PREFIX, text("Failed to reload: Please check your console.")));
        }

        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "reload";
    }
}
