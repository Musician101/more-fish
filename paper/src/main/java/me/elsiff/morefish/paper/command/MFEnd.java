package me.elsiff.morefish.paper.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.common.configuration.Lang.ALREADY_STOPPED;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_STOP;

class MFEnd extends MFCommand implements LiteralCommand {

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return testAdmin(sender);
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (getCompetition().isEnabled()) {
            getCompetitionHost().closeCompetition();
            if (!getConfig().getBoolean("messages.broadcast-stop", false)) {
                sender.sendMessage(CONTEST_STOP);
            }
        }
        else {
            sender.sendMessage(ALREADY_STOPPED);
        }

        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "end";
    }
}
