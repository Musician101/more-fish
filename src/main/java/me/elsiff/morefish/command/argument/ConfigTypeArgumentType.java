package me.elsiff.morefish.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.elsiff.morefish.command.argument.ConfigTypeArgumentType.ConfigType;
import me.elsiff.morefish.util.EnumUtils;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class ConfigTypeArgumentType implements CustomArgumentType.Converted<ConfigType, String> {

    private static final DynamicCommandExceptionType INVALID_CONFIG_TYPE_ERROR = new DynamicCommandExceptionType(type -> MessageComponentSerializer.message().serialize(Component.text(type + " is not a valid config type.")));

    public static ConfigType getConfigType(CommandContext<CommandSourceStack> context, String key) {
        return context.getArgument(key, ConfigType.class);
    }

    @Override
    public ConfigType convert(String nativeType) throws CommandSyntaxException {
        return EnumUtils.getOrThrow(nativeType, ConfigType.class, INVALID_CONFIG_TYPE_ERROR.create(nativeType));
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return EnumUtils.suggestions(builder, ConfigType.class);
    }

    public enum ConfigType {
        CONFIG,
        FISH,
        LANG
    }
}
