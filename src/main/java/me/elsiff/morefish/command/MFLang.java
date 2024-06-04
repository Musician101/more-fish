package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.MFTextInput;
import me.elsiff.morefish.command.argument.LangKeyArgumentType;
import me.elsiff.morefish.command.argument.LangKeyValueArgumentType;
import me.elsiff.morefish.text.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
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
        return "/mf lang edit|test <key>";
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    @Override
    public @NotNull List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new EditCommand(), new TestCommand());
    }

    static class EditCommand implements LiteralCommand {

        @Override
        public boolean canUse(@NotNull CommandSender sender) {
            return sender instanceof Player;
        }

        @Override
        public @NotNull List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
            return List.of(new ArgumentCommand<LangKeyValueArgumentType.Holder>() {

                @Override
                public int execute(@NotNull CommandContext<CommandSender> context) {
                    Player ply = (Player) context.getSource();
                    LangKeyValueArgumentType.Holder value = context.getArgument("key", LangKeyValueArgumentType.Holder.class);
                    TagResolver tagResolver = TagResolver.resolver(Lang.tagResolver("raw-text", Tag.styling(ClickEvent.clickEvent(Action.SUGGEST_COMMAND, value.rawValue()))), Lang.tagResolver("parsed-text", Lang.replace(value.rawValue())), Lang.tagResolver("lang-key", value.key()));
                    ply.sendMessage(Lang.replace("<mf-lang:command-lang-edit-message>", tagResolver));
                    new MFTextInput(ply) {

                        @Override
                        protected void process(String message) {
                            if (message.equals("!cancel")) {
                                close(true);
                                return;
                            }

                            try {
                                Lang.update(value.key(), message);
                            }
                            catch (IOException e) {
                                close(true);
                                getPlugin().getSLF4JLogger().error("An error occurred while attempting to update lang.yml", e);
                                player.sendMessage(Lang.replace("<mf-lang:command-lang-edit-error>"));
                            }
                        }
                    };
                    return 1;
                }

                @Override
                public @NotNull String name() {
                    return "key";
                }

                @Override
                public @NotNull ArgumentType<LangKeyValueArgumentType.Holder> type() {
                    return new LangKeyValueArgumentType();
                }
            });
        }

        @Override
        public @NotNull String name() {
            return "edit";
        }
    }

    static class TestCommand implements LiteralCommand {

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

        @Override
        public @NotNull String name() {
            return "test";
        }
    }
}
