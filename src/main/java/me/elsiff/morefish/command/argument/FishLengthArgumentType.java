package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.FishType;
import org.bukkit.command.CommandSender;

public class FishLengthArgumentType implements ArgumentType<Double> {

    public static double get(@Nonnull CommandContext<CommandSender> context, @Nonnull FishType fishType) throws CommandSyntaxException {
        double length = context.getArgument("length", Double.class);
        if (fishType.lengthMin() <= length && length <= fishType.lengthMax()) {
            return length;
        }

        throw new SimpleCommandExceptionType(() -> length + " is outside the bounds of " + fishType.name()).create();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        FishType fishType = context.getArgument("fish", FishType.class);
        builder.suggest(String.valueOf(fishType.lengthMin()), () -> "Minimum length.");
        builder.suggest(String.valueOf(fishType.lengthMax()), () -> "Maximum length.");
        return builder.buildFuture();
    }

    @Override
    public Double parse(StringReader reader) throws CommandSyntaxException {
        return reader.readDouble();
    }
}
