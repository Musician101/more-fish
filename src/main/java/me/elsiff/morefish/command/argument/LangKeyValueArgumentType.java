package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.elsiff.morefish.text.Lang;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class LangKeyValueArgumentType implements ArgumentType<LangKeyValueArgumentType.Holder> {

    @Override
    public LangKeyValueArgumentType.Holder parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readString();
        return new LangKeyValueArgumentType.Holder() {
            @Override
            public @NotNull String key() {
                return string;
            }
        };
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Lang.keys().stream().filter(s -> s.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    public interface Holder {

        @NotNull
        String key();

        @NotNull
        default String rawValue() {
            return Lang.raw(key());
        }
    }
}
