package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.elsiff.morefish.command.argument.FishRecordsTypeArgumentType.FishRecordsType;
import me.elsiff.morefish.util.EnumUtils;

import java.util.concurrent.CompletableFuture;

public class FishRecordsTypeArgumentType implements ArgumentType<FishRecordsType> {

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return EnumUtils.suggestions(builder, FishRecordsType.class);
    }

    @Override
    public FishRecordsType parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();
        return EnumUtils.getOrThrow(name, FishRecordsType.class, new SimpleCommandExceptionType(() -> name + " is not a valid record type.").createWithContext(reader));
    }

    public enum FishRecordsType {
        ALLTIME,
        COMPETITION
    }
}
