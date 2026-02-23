package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.elsiff.morefish.records.FishRecord;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class UUIDArgumentType implements CustomArgumentType<UUID, UUID> {

    public static UUID getUUID(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, UUID.class);
    }

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
    public ArgumentType<UUID> getNativeType() {
        return ArgumentTypes.uuid();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Stream.concat(getPlugin().getFishingLogs().getRecords().stream(), getPlugin().getCompetition().getRecords().stream()).map(FishRecord::fisher).distinct().map(Bukkit::getOfflinePlayer).filter(o -> matches(builder, o)).map(o -> {
            String name = o.getName();
            return name == null ? o.getUniqueId().toString() : name;
        }).forEach(builder::suggest);
        return builder.buildFuture();
    }

    private boolean matches(SuggestionsBuilder builder, OfflinePlayer o) {
        String uuid = o.getUniqueId().toString();
        String name = o.getName();
        String remaining = builder.getRemaining();
        return (name != null && name.toLowerCase().startsWith(remaining.toLowerCase())) || uuid.startsWith(remaining.toLowerCase());
    }
}
