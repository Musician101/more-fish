package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
class MFHelp implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private final PaperLiteralCommand.AdventureFormat root;

    public MFHelp(PaperLiteralCommand.AdventureFormat root) {
        this.root = root;
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
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

        Component header = Component.translatable("morefish.command.help.header", Argument.string("authors", authorsString), Argument.string("plugin-display-name", meta.getDisplayName()));
        sendMessage(context, header);
        root.children().stream().filter(cmd -> cmd.canUse(source)).forEach(cmd -> {
            ComponentLike commandUsage = Argument.component("command-usage", cmd.usage(source));
            ComponentLike commandDescription = Argument.component("command-description", cmd.description(source));
            Component message = Component.translatable("morefish.command.help.info", commandUsage, commandDescription);
            sendMessage(context, message);
        });
        return 1;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.help.description");
    }
}
