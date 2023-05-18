package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.musician101.bukkitier.command.LiteralCommand;
import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;

public class MFTop extends MFCommand implements LiteralCommand {

    @Nonnull
    @Override
    public String name() {
        return "top";
    }

    @Override
    public int execute(@Nonnull CommandContext<CommandSender> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource();
        getCompetitionHost().informAboutRanking(sender);
        return 1;
    }
}
