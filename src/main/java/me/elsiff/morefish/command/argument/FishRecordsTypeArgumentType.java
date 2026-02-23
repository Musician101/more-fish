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
import me.elsiff.morefish.command.argument.FishRecordsTypeArgumentType.FishRecordsType;
import me.elsiff.morefish.util.EnumUtils;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class FishRecordsTypeArgumentType implements CustomArgumentType.Converted<FishRecordsType, String> {

    private static final DynamicCommandExceptionType INVALID_RECORD_TYPE_ERROR = new DynamicCommandExceptionType(type -> MessageComponentSerializer.message().serialize(Component.text(type + " is not a valid record type.")));

    public static FishRecordsType getRecordType(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, FishRecordsType.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return EnumUtils.suggestions(builder, FishRecordsType.class);
    }

    @Override
    public FishRecordsType convert(String nativeType) throws CommandSyntaxException {
        return EnumUtils.getOrThrow(nativeType, FishRecordsType.class, INVALID_RECORD_TYPE_ERROR.create(nativeType));
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public enum FishRecordsType {
        ALLTIME,
        COMPETITION
    }
}
