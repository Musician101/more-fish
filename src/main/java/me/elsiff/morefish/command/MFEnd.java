package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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
            sender.sendMessage(Lang.CONTEST_STOP);
        }
        else {
            sender.sendMessage(Lang.ALREADY_STOPPED);
        }

        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "end";
    }
}
