package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.util.EnumUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class SortArgumentType implements ArgumentType<SortType> {

    @Override
    public SortType parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();
        return EnumUtils.getOrThrow(name, SortType.class, new SimpleCommandExceptionType(() -> name + " is not a valid record type.").createWithContext(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return EnumUtils.suggestions(builder, SortType.class);
    }

    public enum SortType {
        LENGTH,
        NAME,
        RARITY,
        TIMESTAMP,
        PLAYER;

        @NotNull
        public Comparator<FishRecord> sorter() {
            return switch (this) {
                case LENGTH -> FishRecord::compareTo;
                case NAME -> Comparator.comparing(FishRecord::getFishName);
                case RARITY -> Comparator.comparingDouble(FishRecord::getRarityProbability).reversed();
                case TIMESTAMP -> Comparator.comparingLong(FishRecord::timestamp);
                case PLAYER -> Comparator.comparing(FishRecord::fisher);
            };
        }
    }
}
