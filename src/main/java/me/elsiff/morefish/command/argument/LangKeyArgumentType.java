package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

import static me.elsiff.morefish.text.Lang.keys;
import static me.elsiff.morefish.text.Lang.raw;

public class LangKeyArgumentType implements ArgumentType<String> {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return raw(reader.readString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        keys().stream().filter(s -> s.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
