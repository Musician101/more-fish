package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import io.musician101.bukkitier.command.help.HelpSubCommand;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.command.MFMain.cmdInfo;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

class MFHelp extends HelpSubCommand {

    public MFHelp(@NotNull LiteralCommand root) {
        super(root, getPlugin());
    }

    @NotNull
    @Override
    protected Component commandInfo(@NotNull Command<? extends ArgumentBuilder<CommandSender, ?>> command, @NotNull CommandSender sender) {
        return cmdInfo(command, sender);
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    @Override
    protected Component header() {
        PluginMeta meta = plugin.getPluginMeta();
        List<String> authors = meta.getAuthors();
        int last = authors.size() - 1;
        String authorsString = switch (last) {
            case 0 -> authors.get(0);
            case 1 -> String.join(" and ", authors);
            default -> String.join(", and ", String.join(", ", authors.subList(0, last)), authors.get(last));
        };
        String string = "<dark_aqua>> ===== <aqua><hover:show_text:'<color:#BDB76B>Developed by " + authorsString + "'>" + meta.getDisplayName() + "<dark_aqua> ===== <<newline><gold>Click a command for more info.<newline><click:open_url:https://github.com/Musician101/more-fish/wiki>Click here to visit our wiki.";
        return miniMessage().deserialize(string);
    }
}
