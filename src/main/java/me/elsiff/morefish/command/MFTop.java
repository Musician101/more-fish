package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.musician101.bukkitier.command.LiteralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MFTop extends MFCommand implements LiteralCommand {

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource();
        getCompetitionHost().informAboutRanking(sender);
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "top";
    }
}
