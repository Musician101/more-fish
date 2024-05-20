package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.raw;
import static me.elsiff.morefish.text.Lang.replace;

class MFEnd implements LiteralCommand {

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return raw("command-end-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf end";
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (getPlugin().getCompetition().isEnabled()) {
            getPlugin().getCompetitionHost().closeCompetition();
            sender.sendMessage(replace("<mf-lang:contest-stop>"));
        }
        else {
            sender.sendMessage(replace("<mf-lang:already-stopped>"));
        }

        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "end";
    }
}
