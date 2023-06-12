package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;

import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;

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
        sender.sendMessage(join(PREFIX, text("The records has been cleared successfully.")));
        return 1;
    }
}
