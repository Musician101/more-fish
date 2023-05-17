package me.elsiff.morefish.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.FishType;
import org.bukkit.command.CommandSender;

import static me.elsiff.morefish.MoreFish.getPlugin;

public class FishTypeArgument implements ArgumentType<FishType> {

    @Nonnull
    public static FishType get(@Nonnull CommandContext<CommandSender> context) {
        return context.getArgument("fish", FishType.class);
    }

    private Stream<FishType> fishes() {
        return getPlugin().getFishTypeTable().getTypes().stream();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        fishes().map(FishType::name).filter(name -> name.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public FishType parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();
        return fishes().filter(f -> f.name().equals(name)).findFirst().orElseThrow(() -> new SimpleCommandExceptionType(() -> name + " is not a valid fish.").createWithContext(reader));
    }
}
