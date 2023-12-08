package me.elsiff.morefish.sponge.command;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.plugin.metadata.PluginMetadata;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

class MFHelp extends MFCommand {

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        PluginMetadata pmd = getPlugin().getPluginContainer().metadata();
        String pluginName = pmd.name().orElse(pmd.id());
        Component prefix = text("[" + pluginName + "] ", AQUA);
        context.sendMessage(join(prefix, text("> ===== ", DARK_AQUA), text(pluginName + ' ', style(AQUA, BOLD)), text('v' + pmd.version().toString(), AQUA), text(" ===== <", DARK_AQUA)));
        Component label = join(prefix, text("/mf"));
        context.sendMessage(join(label, text(" help")));
        //TODO automate?
        if (testAdmin(context)) {
            context.sendMessage(join(label, text(" begin [runningTime(sec)]")));
            context.sendMessage(join(label, text(" suspend")));
            context.sendMessage(join(label, text(" end")));
            context.sendMessage(join(label, text(" give <ServerPlayer> <fish> [length] [amount]")));
            context.sendMessage(join(label, text(" clear")));
            context.sendMessage(join(label, text(" reload")));
        }

        if (context.hasPermission("morefish.shop") || testAdmin(context)) {
            context.sendMessage(join(label, text(" shop"), text((testAdmin(context) ? " [player]" : ""))));
        }

        context.sendMessage(join(label, text(" contraband")));
        context.sendMessage(join(label, text("scoreboard")));
        context.sendMessage(join(label, text(" top")));
        return CommandResult.success();
    }

    @NotNull
    @Override
    public String getName() {
        return "help";
    }
}
