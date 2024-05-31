package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.command.argument.LangKeyArgumentType;
import me.elsiff.morefish.text.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver;

public class MFLang implements LiteralCommand {

    @Override
    public @NotNull String description(@NotNull CommandSender sender) {
        return Lang.raw("command-lang-description");
    }

    @Override
    public @NotNull String name() {
        return "lang";
    }

    @Override
    public @NotNull String usage(@NotNull CommandSender sender) {
        return "/mf lang <key> [<tagResolvers>]";
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    @Override
    public @NotNull List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new ArgumentCommand<String>() {

            @Override
            public int execute(@NotNull CommandContext<CommandSender> context) {
                CommandSender sender = context.getSource();
                String value = context.getArgument("key", String.class);
                TagResolver t = resolver(Lang.tagResolver("raw-text", value), Lang.tagResolver("parsed-text", Lang.replace(value)));
                Component message = Lang.replace("<mf-lang:command-lang-message>", t);
                sender.sendMessage(message);
                return 1;
            }

            @Override
            public @NotNull String name() {
                return "key";
            }

            @Override
            public @NotNull ArgumentType<String> type() {
                return new LangKeyArgumentType();
            }
        });
    }
}
