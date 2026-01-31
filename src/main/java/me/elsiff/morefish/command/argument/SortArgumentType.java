package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.records.FishRecord;
import me.elsiff.morefish.util.EnumUtils;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class SortArgumentType implements CustomArgumentType.Converted<SortType, String> {

    private static final DynamicCommandExceptionType INVALID_SORT_TYPE_ERROR = new DynamicCommandExceptionType(type -> MessageComponentSerializer.message().serialize(Component.text(type + " is not a valid sort type.")));

    public static SortType getSortType(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, SortType.class);
    }

    @Override
    public SortType convert(String nativeType) throws CommandSyntaxException {
        return EnumUtils.getOrThrow(nativeType, SortType.class, INVALID_SORT_TYPE_ERROR.create(nativeType));
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
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

        public Comparator<FishRecord> natural() {
            return switch (this) {
                case LENGTH -> FishRecord::compareTo;
                case NAME -> Comparator.comparing(r -> r.fish().type().name());
                case RARITY -> Comparator.<FishRecord, FishRarity>comparing(r -> r.fish().rarity()).reversed();
                case TIMESTAMP -> Comparator.comparingLong(FishRecord::timestamp);
                case PLAYER -> Comparator.comparing(FishRecord::fisher);
            };
        }

        public Comparator<FishRecord> reversed() {
            return natural().reversed();
        }
    }
}
