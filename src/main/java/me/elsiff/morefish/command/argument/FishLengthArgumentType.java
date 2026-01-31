package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.musician101.musicommand.core.command.CommandException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.elsiff.morefish.fish.FishType;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class FishLengthArgumentType implements CustomArgumentType<Float, Float> {

    public static float getLength(CommandContext<CommandSourceStack> context, FishType fishType) throws CommandException {
        float length = context.getArgument("length", Float.class);
        if (fishType.minLength() <= length && length <= fishType.maxLength()) {
            return length;
        }

        throw new CommandException(length + " is outside the bounds of " + fishType.name());
    }

    @Override
    public ArgumentType<Float> getNativeType() {
        return FloatArgumentType.floatArg(0);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        FishType fishType = context.getArgument("fish", FishType.class);
        builder.suggest(String.valueOf(fishType.minLength()), () -> "Minimum length.");
        builder.suggest(String.valueOf(fishType.maxLength()), () -> "Maximum length.");
        return builder.buildFuture();
    }

    @Override
    public Float parse(StringReader reader) throws CommandSyntaxException {
        return reader.readFloat();
    }
}
