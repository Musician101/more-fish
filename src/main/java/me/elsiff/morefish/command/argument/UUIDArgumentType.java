package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.elsiff.morefish.records.FishRecord;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;

public class UUIDArgumentType implements ArgumentType<UUID> {

    @Override
    public UUID parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readString();
        try {
            return UUID.fromString(string);
        }
        catch (IllegalArgumentException ignored) {

        }

        return Bukkit.getOfflinePlayer(string).getUniqueId();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Stream.concat(getPlugin().getFishingLogs().getRecords().stream(), getPlugin().getCompetition().getRecords().stream()).map(FishRecord::fisher).distinct().map(Bukkit::getOfflinePlayer).filter(o -> {
            String uuid = o.getUniqueId().toString();
            String name = o.getName();
            String remaining = builder.getRemaining();
            return (name != null && name.toLowerCase().startsWith(remaining.toLowerCase())) || uuid.startsWith(remaining.toLowerCase());
        }).map(o -> {
            String name = o.getName();
            return name == null ? o.getUniqueId().toString() : name;
        }).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
