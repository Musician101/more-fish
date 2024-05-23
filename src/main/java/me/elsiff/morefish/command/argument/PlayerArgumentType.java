package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class PlayerArgumentType implements ArgumentType<Player> {

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public Player parse(StringReader stringReader) throws CommandSyntaxException {
        return Bukkit.getPlayerExact(stringReader.readString());
    }
}
