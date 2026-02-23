package me.elsiff.morefish.records;

import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@NullMarked
public record FishRecord(UUID fisher, Fish fish,
                         long timestamp) implements Comparable<FishRecord>, ComponentLike, TagResolver {

    public int compareTo(FishRecord other) {
        return Double.compare(fish.length(), other.fish.length());
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (!has(name)) {
            return null;
        }

        String value = arguments.popOr(name + " needs at least 1 argument").value();
        return switch (value) {
            case "date" ->
                    TagResolverUtil.fromResolver(Formatter.date(value, ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())), value, arguments, ctx);
            case "fish-type" -> fish.type().resolve(value, arguments, ctx);
            case "fisher" -> TagResolverUtil.playerNameTag(Bukkit.getOfflinePlayer(fisher));
            case "length" -> TagResolverUtil.numberTag(value, fish.length(), arguments, ctx);
            case "rarity-name" -> fish.type().rarity().resolve(value, arguments, ctx);
            default -> null;
        };
    }

    @Override
    public boolean has(String name) {
        return name.equals("fish-record");
    }

    @Override
    public Component asComponent() {
        return (VirtualComponent) Argument.tagResolver(this);
    }
}
