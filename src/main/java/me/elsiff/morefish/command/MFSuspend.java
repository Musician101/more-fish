package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.text.Lang;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public class MFSuspend implements LiteralCommand {

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return "Suspend the competition.";
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf suspend";
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (!getPlugin().getCompetition().isDisabled()) {
            getPlugin().getCompetitionHost().closeCompetition(true);
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
        return "suspend";
    }
}
