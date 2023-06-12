package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;

public class MFReload extends MFCommand implements LiteralCommand {

    @Nonnull
    @Override
    public String name() {
        return "reload";
    }

    @Override
    public boolean canUse(@Nonnull CommandSender sender) {
        return testAdmin(sender);
    }

    @Override
    public int execute(@Nonnull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        try {
            getPlugin().applyConfig();
            sender.sendMessage(join(PREFIX, text("Reloaded the config successfully.")));
        }
        catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(join(PREFIX, text("Failed to reload: Please check your console.")));
        }

        return 1;
    }
}
