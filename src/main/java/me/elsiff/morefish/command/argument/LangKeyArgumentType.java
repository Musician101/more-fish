package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.elsiff.morefish.text.Lang;

import java.util.concurrent.CompletableFuture;

public class LangKeyArgumentType implements ArgumentType<String> {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return Lang.raw(reader.readString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Lang.keys().stream().filter(s -> s.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
