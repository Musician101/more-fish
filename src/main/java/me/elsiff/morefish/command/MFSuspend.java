package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.command.CommandSender;

public class MFSuspend extends MFCommand implements LiteralCommand {

    @Nonnull
    @Override
    public String name() {
        return "suspend";
    }

    @Override
    public boolean canUse(@Nonnull CommandSender sender) {
        return testAdmin(sender);
    }

    @Override
    public int execute(@Nonnull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (!getCompetition().isDisabled()) {
            getCompetitionHost().closeCompetition(true);
            if (!getConfig().getBoolean("messages.broadcast-stop", false)) {
                sender.sendMessage(Lang.CONTEST_STOP);
            }
        }
        else {
            sender.sendMessage(Lang.ALREADY_STOPPED);
        }

        return 1;
    }
}
