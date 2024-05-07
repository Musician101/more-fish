package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

public class FishArgumentType implements ArgumentType<List<FishRecord>> {

    @Override
    public List<FishRecord> parse(StringReader reader) {
        String string = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return getRecords().stream().filter(r -> r.getFishName().equals(string)).collect(Collectors.toList());
    }

    private List<FishRecord> getRecords() {
        return getPlugin().getFishingLogs().getRecords();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        getRecords().stream().map(FishRecord::getFishName).filter(s -> s.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
