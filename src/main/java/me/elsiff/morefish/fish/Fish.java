package me.elsiff.morefish.fish;

import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Fish(FishType type, double length) implements TagResolver {

    public FishRarity rarity() {
        return type.rarity();
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            String value = arguments.popOr(name + " needs at least 1 argument").value();
            return switch (value) {
                case "length" -> TagResolverUtil.numberTag(value, length, arguments, ctx);
                case "fish-type" -> Tag.selfClosingInserting(ctx.deserialize(type.displayName()));
                case "fish-rarity" -> Tag.selfClosingInserting(ctx.deserialize(rarity().displayName()));
                default -> null;
            };
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("fish");
    }
}
