package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

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

        NodePath path = NodePath.path("command", "help");
        TagResolver headerResolver = TagResolver.resolver(Placeholder.parsed("authors", authorsString), Placeholder.parsed("plugin-display-name", meta.getDisplayName()));
        sendMessage(context, lang().getComponent(path.withAppendedChild("header"), headerResolver));
        root.children().stream().filter(cmd -> cmd.canUse(source)).forEach(cmd -> {
            TagResolver commandResolver = TagResolver.resolver(Placeholder.component("command-usage", cmd.usage(source)), Placeholder.component("command-description", cmd.description(source)));
            sendMessage(context, lang().getComponent(path.withAppendedChild("info"), commandResolver));
        });
        return 1;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return lang().getComponent("command", "help", "description");
    }
}
