package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import me.elsiff.morefish.command.MFClear.RecordsType;

public class RecordsArgumentType implements ArgumentType<RecordsType> {

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Arrays.stream(RecordsType.values()).map(RecordsType::toString).map(String::toLowerCase).filter(s -> s.startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public RecordsType parse(StringReader stringReader) throws CommandSyntaxException {
        String s = stringReader.readString();
        return RecordsType.get(s).orElseThrow(() -> new SimpleCommandExceptionType(() -> s + " is not a valid record type.").createWithContext(stringReader));
    }
}
