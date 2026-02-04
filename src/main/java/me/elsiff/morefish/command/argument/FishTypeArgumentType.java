package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.elsiff.morefish.fish.FishType;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class FishTypeArgumentType implements CustomArgumentType.Converted<FishType, NamespacedKey> {

    public static FishType getFishType(CommandContext<CommandSourceStack> context) {
        return context.getArgument("fish", FishType.class);
    }

    private Stream<FishType> fishes() {
        return getPlugin().getFishTypeTable().getTypes().stream();
    }

    @Override
    public ArgumentType<NamespacedKey> getNativeType() {
        return ArgumentTypes.namespacedKey();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        fishes().map(FishType::getKey).map(NamespacedKey::asString).filter(key -> key.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public FishType convert(NamespacedKey nativeType) throws CommandSyntaxException {
        return getPlugin().getFishTypeTable().types().get(nativeType).orElseThrow(() -> new SimpleCommandExceptionType(() -> nativeType + " is not a valid fish type.").create());
    }
}
