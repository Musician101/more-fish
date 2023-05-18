package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.command.CommandSender;

import static me.elsiff.morefish.MoreFish.getPlugin;

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
            sender.sendMessage(Lang.RELOAD_CONFIG);
        }
        catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(Lang.FAILED_TO_RELOAD);
        }

        return 1;
    }
}
