package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.raw;
import static me.elsiff.morefish.text.Lang.replace;
import static me.elsiff.morefish.text.Lang.tagResolver;
import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver;

class MFHelp implements LiteralCommand {

    private final LiteralCommand root;

    public MFHelp(@NotNull LiteralCommand root) {
        this.root = root;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        PluginMeta meta = getPlugin().getPluginMeta();
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
        sender.sendMessage(replace("<mf-lang:command-help-header>", resolver(tagResolver("authors", authorsString), tagResolver("plugin-display-name", meta.getDisplayName()))));
        root.arguments().stream().filter(cmd -> cmd.canUse(sender)).forEach(cmd -> sender.sendMessage(replace("<mf-lang:command-help-info>", resolver(tagResolver("command-usage", root.usage(sender) + " " + cmd.name()), tagResolver("command-description", cmd.description(sender))))));
        return 1;
    }

    @Override
    public @NotNull String name() {
        return "help";
    }

    @Override
    public @NotNull String description(@NotNull CommandSender sender) {
        return raw("command-help-description");
    }
}
