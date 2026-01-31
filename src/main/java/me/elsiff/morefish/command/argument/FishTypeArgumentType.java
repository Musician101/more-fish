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
import me.elsiff.morefish.fish.FishType;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class FishTypeArgumentType implements CustomArgumentType.Converted<FishType, String> {

    private static final DynamicCommandExceptionType INVALID_FISH_TYPE_ERROR = new DynamicCommandExceptionType(type -> MessageComponentSerializer.message().serialize(Component.text(type + " is not a valid fish type.")));

    public static FishType getFishType(CommandContext<CommandSourceStack> context) {
        return context.getArgument("fish", FishType.class);
    }

    private Stream<FishType> fishes() {
        return getPlugin().getFishTypeTable().getTypes().stream();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        fishes().map(FishType::name).filter(name -> name.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public FishType convert(String nativeType) throws CommandSyntaxException {
        return fishes().filter(f -> f.name().equals(nativeType)).findFirst().orElseThrow(() -> INVALID_FISH_TYPE_ERROR.create(nativeType));
    }
}
