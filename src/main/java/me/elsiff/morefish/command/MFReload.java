package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.raw;
import static me.elsiff.morefish.text.Lang.replace;

class MFReload implements LiteralCommand {

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return raw("command-reload-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf reload";
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        try {
            getPlugin().applyConfig();
            sender.sendMessage(replace("<mf-lang:command-reload-success>"));
        }
        catch (Exception e) {
            getPlugin().getSLF4JLogger().error("An error occurred while reloading the config.", e);
            sender.sendMessage(replace("<mf-lang:command-reload-fail>"));
        }

        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "reload";
    }
}
