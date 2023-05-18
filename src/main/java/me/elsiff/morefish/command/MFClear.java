package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.command.CommandSender;

class MFClear extends MFCommand implements LiteralCommand {

    @Nonnull
    @Override
    public String name() {
        return "clear";
    }

    @Override
    public boolean canUse(@Nonnull CommandSender sender) {
        return testAdmin(sender);
    }

    @Override
    public int execute(@Nonnull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        getCompetition().clearRecords();
        sender.sendMessage(Lang.CLEAR_RECORDS);
        return 1;
    }
}
