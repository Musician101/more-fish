package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.elsiff.morefish.command.argument.RecordsArgumentType.Holder;
import me.elsiff.morefish.records.FishRecord;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class RecordsArgumentType implements CustomArgumentType.Converted<Holder, String> {

    public static List<FishRecord> getRecords(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Holder.class).get();
    }

    @Override
    public Holder convert(String nativeType) {
        return () -> getRecords().stream().filter(r -> r.fish().type().name().equals(nativeType)).collect(Collectors.toList());
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    private List<FishRecord> getRecords() {
        return getPlugin().getFishingLogs().getRecords();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        getRecords().stream().map(r -> r.fish().type().name()).filter(s -> s.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @NullMarked
    public interface Holder {

        List<FishRecord> get();
    }
}
