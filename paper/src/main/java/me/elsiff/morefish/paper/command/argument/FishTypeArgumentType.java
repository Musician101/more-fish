package me.elsiff.morefish.paper.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import me.elsiff.morefish.paper.fishing.PaperFishType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public class FishTypeArgumentType implements ArgumentType<PaperFishType> {

    @NotNull
    public static PaperFishType get(@NotNull CommandContext<CommandSender> context) {
        return context.getArgument("fish", PaperFishType.class);
    }

    private Stream<PaperFishType> fishes() {
        return getPlugin().getFishTypeTable().getTypes().stream();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        fishes().map(PaperFishType::name).filter(name -> name.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public PaperFishType parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();
        return fishes().filter(f -> f.name().equals(name)).findFirst().orElseThrow(() -> new SimpleCommandExceptionType(() -> name + " is not a valid fish.").createWithContext(reader));
    }
}
