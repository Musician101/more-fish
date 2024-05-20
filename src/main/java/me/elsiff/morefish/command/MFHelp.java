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
import static me.elsiff.morefish.text.Lang.replace;
import static me.elsiff.morefish.text.Lang.tagResolver;
import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver;

class MFHelp extends HelpSubCommand {

    public MFHelp(@NotNull LiteralCommand root) {
        super(root, getPlugin());
    }

    @Override
    protected @NotNull Component commandInfo(@NotNull Command<? extends ArgumentBuilder<CommandSender, ?>> root, @NotNull Command<? extends ArgumentBuilder<CommandSender, ?>> command, @NotNull CommandSender sender) {
        return replace("<mf-lang:command-help-info>", resolver(tagResolver("command-usage", root.usage(sender) + command.name()), tagResolver("command-description", command.description(sender))));
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    @Override
    protected Component header() {
        PluginMeta meta = plugin.getPluginMeta();
        List<String> authors = meta.getAuthors();
        String authorsString = "";
        if (!authors.isEmpty()) {
            int last = authors.size() - 1;
            authorsString = switch (last) {
                case 0 -> authors.getFirst();
                case 1 -> String.join(" and ", authors);
                default -> String.join(", and ", String.join(", ", authors.subList(0, last)), authors.get(last));
            };
        }

        return replace("<mf-lang:command-help-header>", resolver(tagResolver("authors", authorsString), tagResolver("plugin-display-name", meta.getDisplayName())));
    }
}
