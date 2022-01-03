package io.musician101.morefish.spigot.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.musician101.morefish.spigot.SpigotMoreFish;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerArgumentType implements ArgumentType<Player> {

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public Player parse(StringReader stringReader) throws CommandSyntaxException {
        String name = stringReader.getString();
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
            throw new SimpleCommandExceptionType(() -> SpigotMoreFish.getInstance().getPluginConfig().getLangConfig().format("player-not-found").replace(Collections.singletonMap("%s", name)).output()).createWithContext(stringReader);
        }

        return player;
    }
}
