package me.elsiff.morefish.paper.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.common.configuration.Lang;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

class MFClear extends MFCommand implements LiteralCommand {

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return testAdmin(sender);
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        getCompetition().clearRecords();
        sender.sendMessage(Lang.join(Lang.PREFIX, text("The records has been cleared successfully.")));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "clear";
    }
}
