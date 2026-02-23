package me.elsiff.morefish.lang;

import me.elsiff.morefish.fish.Fish;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.OfflinePlayer;
import org.codehaus.plexus.util.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

@NullMarked
public interface ArgumentUtil {

    static ComponentLike namedTextColor(NamedTextColor color) {
        return Argument.tagResolver(TagResolver.resolver("named-color", (a, c) -> {
            if (a.hasNext() && a.pop().isTrue()) {
                String name = StringUtils.capitaliseAllWords(color.toString().replaceAll("_", " "));
                return Tag.selfClosingInserting(Component.text(name, color));
            }

            return Tag.styling(b -> b.color(color));
        }));
    }

    static ComponentLike timeRemaining(long second) {
        StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofSeconds(second);
        if (duration.toMinutes() > 0L) {
            builder.append(duration.toMinutes()).append("m").append(" ");
        }

        builder.append(duration.getSeconds() % (long) 60).append("s");
        return Argument.string("time-remaining", builder.toString());
    }

    static ComponentLike player(OfflinePlayer player) {
        return Argument.tagResolver(TagResolverUtil.playerResolver(player));
    }

    static ComponentLike fish(Fish fish) {
        return Argument.tagResolver(TagResolver.resolver(fish, fish.rarity(), fish.type()));
    }

    static ComponentLike ordinal(int number) {
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

        return Argument.string("ordinal", number + suffix);
    }

    static ComponentLike error(@Nullable String message) {
        return Argument.string("error", message == null ? "No message provided. Check the console." : message);
    }
}
