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
        return Lang.raw("command-suspend-description");
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
        if (getPlugin().getCompetition().isEnabled()) {
            getPlugin().getCompetitionHost().closeCompetition(true);
            sender.sendMessage(Lang.replace("<mf-lang:contest-stop>"));
        }
        else {
            sender.sendMessage(Lang.replace("<mf-lang:already-stopped>"));
        }

        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "suspend";
    }
}
