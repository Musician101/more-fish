package me.elsiff.morefish.lang;

import me.elsiff.morefish.fish.Fish;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.Tag.Argument;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.codehaus.plexus.util.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static me.elsiff.morefish.MoreFish.lang;

@SuppressWarnings("PatternValidation")
@NullMarked
public interface TagResolverUtil {

    static TagResolver error(@Nullable String message) {
        return Placeholder.parsed("error", message == null ? "No message provided. Check the console." : message);
    }

    static <V> Tag fromList(List<V> values, ArgumentQueue argumentQueue, Context context, Function<V, Tag> tagMapper) {
        Argument arg = argumentQueue.popOr("Not enough arguments");
        if (arg.value().equals("size")) {
            return Tag.preProcessParsed(values.size() + "");
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
            return Tag.preProcessParsed(map.size() + "");
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
            return Tag.preProcessParsed(map.size() + "");
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

    static TagResolver playerNameResolver(OfflinePlayer player) {
        return TagResolver.resolver("player", playerNameTag(player));
    }

    static TagResolver catcher(OfflinePlayer player, Fish fish) {
        return TagResolver.resolver(playerNameResolver(player), fish, fish.rarity(), fish.type());
    }

    static Tag playerNameTag(OfflinePlayer player) {
        String name = player.getName();
        if (name == null) {
            name = player.getUniqueId().toString();
        }

        return Tag.selfClosingInserting(Component.text(name));
    }

    static TagResolver timeRemaining(long second) {
        StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofSeconds(second);
        if (duration.toMinutes() > 0L) {
            builder.append(duration.toMinutes()).append("m").append(" ");
        }

        builder.append(duration.getSeconds() % (long) 60).append("s");
        return Placeholder.parsed("time-remaining", builder.toString());
    }

    static TagResolver ordinal(int number) {
        String suffix;
        switch (number % 10) {
            case 1 -> suffix = "st";
            case 2 -> suffix = "nd";
            case 3 -> suffix = "rd";
            default -> suffix = "th";
        }

        switch (number % 100) {
            case 11, 12, 13 -> suffix = "th";
        }

        return Placeholder.parsed("ordinal", number + suffix);
    }

    static TagResolver enabled(boolean enabled) {
        return booleanTagResolver("enabled", enabled);
    }

    static TagResolver booleanTagResolver(@TagPattern String name, boolean bool) {
        return TagResolver.resolver(name, (a, c) -> booleanTag(bool, c));
    }

    static Tag booleanTag(boolean bool) {
        return booleanTag(bool, null);
    }

    static Tag booleanTag(boolean bool, @Nullable Context context) {
        Component component;
        if (context != null) {
            component = bool ? context.deserialize("<mf-lang:selected>") : context.deserialize("<mf-lang:not-selected>");
        }
        else {
            NodePath path = NodePath.path("gui", (bool ? "selected" : "not-selected"));
            component = lang().getComponent(path);
        }

        return Tag.selfClosingInserting(component);
    }

    static TagResolver namedTextColor(NamedTextColor color) {
        return TagResolver.resolver("named-color", (a, c) -> {
            if (a.hasNext() && a.pop().isTrue()) {
                String name = StringUtils.capitaliseAllWords(color.toString().replaceAll("_", " "));
                return Tag.selfClosingInserting(Component.text(name, color));
            }

            return Tag.styling(b -> b.color(color));
        });
    }
}
