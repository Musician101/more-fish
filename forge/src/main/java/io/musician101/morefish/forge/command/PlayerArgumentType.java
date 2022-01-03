package io.musician101.morefish.forge.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.musician101.morefish.forge.ForgeMoreFish;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class PlayerArgumentType implements ArgumentType<ServerPlayerEntity> {

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        Stream.of(server.getOnlinePlayerNames()).filter(s -> s.startsWith(builder.getRemaining())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ServerPlayerEntity parse(StringReader stringReader) throws CommandSyntaxException {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        String name = stringReader.getString();
        ServerPlayerEntity player = server.getPlayerList().getPlayerByUsername(name);
        if (player == null) {
            throw new SimpleCommandExceptionType(() -> ForgeMoreFish.getInstance().getPluginConfig().getLangConfig().format("player-not-found").replace(Collections.singletonMap("%s", name)).output().getString()).createWithContext(stringReader);
        }

        return player;
    }
}
