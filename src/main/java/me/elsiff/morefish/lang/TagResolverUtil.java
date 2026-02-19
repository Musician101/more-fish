package me.elsiff.morefish.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.Tag.Argument;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("PatternValidation")
@NullMarked
public interface TagResolverUtil {

    @Nullable
    static <V> Tag fromList(List<V> values, ArgumentQueue argumentQueue, Context context, Function<V, Tag> tagMapper) {
        Argument arg = argumentQueue.popOr("Not enough arguments");
        if (arg.value().equals("size")) {
            return numberTag("size", values.size(), argumentQueue, context);
        }

        ParsingException ex = context.newException("index must be a number greater than 0.");
        int index = arg.asInt().orElseThrow(() -> ex);
        if (index < 0) {
            throw ex;
        }

        if (index >= values.size()) {
            throw context.newException("index is greater than list size.");
        }

        return tagMapper.apply(values.get(index));
    }

    @Nullable
    static <K, V> Tag fromMap(Map<K, V> map, ArgumentQueue argumentQueue, Context context, Function<String, @Nullable K> keyMapper, Function<V, @Nullable Tag> tagMapper) {
        Argument arg = argumentQueue.popOr("Not enough arguments");
        String key = arg.value();
        if (key.equals("size")) {
            return numberTag("size", map.size(), argumentQueue, context);
        }

        K mappedKey = keyMapper.apply(key);
        if (mappedKey == null) {
            throw context.newException("Failed to map " + key);
        }

        V value = map.get(mappedKey);
        if (value == null) {
            throw context.newException(key + " does not exist in map");
        }

        return tagMapper.apply(value);
    }

    @Nullable
    static <K, V> Tag fromMap(Map<K, @Nullable V> map, ArgumentQueue argumentQueue, Context context, @Nullable V defaultValue, Function<String, @Nullable K> keyMapper, Function<V, @Nullable Tag> tagMapper) {
        Argument arg = argumentQueue.popOr("Not enough arguments");
        String key = arg.value();
        if (key.equals("size")) {
            return numberTag("size", map.size(), argumentQueue, context);
        }

        V value = map.getOrDefault(keyMapper.apply(key), defaultValue);
        if (value == null) {
            throw context.newException(key + " does not exist in map");
        }

        return tagMapper.apply(value);
    }

    @Nullable
    static Tag numberTag(String value, @Nullable Number number, ArgumentQueue argumentQueue, Context context) {
        if (number == null) {
            return null;
        }

        return fromResolver(Formatter.number(value, number), value, argumentQueue, context);
    }

    @Nullable
    static Tag fromResolver(TagResolver resolver, String key, ArgumentQueue argumentQueue, Context context) {
        return resolver.resolve(key, argumentQueue, context);
    }

    static TagResolver playerResolver(OfflinePlayer player) {
        return TagResolver.resolver("player", (a, c) -> {
            if (a.hasNext()) {
                return switch (a.pop().value()) {
                    case "head" -> Tag.preProcessParsed("<head:" + player.getUniqueId() + ":outer_layer>");
                    case "name" -> playerNameTag(player);
                    case "uuid" -> Tag.selfClosingInserting(Component.text(player.getUniqueId().toString()));
                    default -> null;
                };
            }

            return playerNameTag(player);
        });
    }

    static Tag playerNameTag(OfflinePlayer player) {
        String name = player.getName();
        if (name == null) {
            name = player.getUniqueId().toString();
        }

        return Tag.selfClosingInserting(Component.text(name));
    }

    @Nullable
    static Tag booleanTag(String value, boolean bool, ArgumentQueue argumentQueue, Context context) {
        return fromResolver(Formatter.booleanChoice(value, bool), value, argumentQueue, context);
    }
}
