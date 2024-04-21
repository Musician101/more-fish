package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

class MFHelp extends MFCommand implements LiteralCommand {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        PluginMeta pluginInfo = getPlugin().getPluginMeta();
        String pluginName = pluginInfo.getName();
        Component prefix = text("[" + pluginName + "] ", AQUA);
        sender.sendMessage(join(prefix, text("> ===== ", DARK_AQUA), text(pluginName + ' ', style(AQUA, BOLD)), text('v' + pluginInfo.getVersion(), AQUA), text(" ===== <", DARK_AQUA)));
        Component label = join(prefix, text("/mf"));
        sender.sendMessage(join(label, text(" help")));

        if (testAdmin(sender)) {
            sender.sendMessage(join(label, text(" clear [alltime|competition] [<player>]")));
            sender.sendMessage(join(label, text(" end")));
            sender.sendMessage(join(label, text(" give <player> <fish> [<length>] [<amount>]")));
            sender.sendMessage(join(label, text(" start [<seconds)>]")));
            sender.sendMessage(join(label, text(" suspend")));
            sender.sendMessage(join(label, text(" reload")));
        }

        sender.sendMessage(join(label, text(" contraband")));
        sender.sendMessage(join(label, text(" fishinglogs [<page>] [<sort>]")));
        sender.sendMessage(join(label, text(" scoreboard")));
        sender.sendMessage(join(label, text(" shop"), text((testAdmin(sender) ? " [<player>]" : ""))));
        sender.sendMessage(join(label, text(" top [alltime [<sort>]|competition]")));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "help";
    }
}
