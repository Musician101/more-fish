package me.elsiff.morefish.fish;

import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Fish(FishType type, double length) implements ComponentLike, TagResolver {

    public FishRarity rarity() {
        return type.rarity();
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            String value = arguments.popOr(name + " needs at least 1 argument").value();
            return switch (value) {
                case "length" -> TagResolverUtil.numberTag(value, length, arguments, ctx);
                case "fish-type" -> Tag.preProcessParsed(type.displayName());
                case "fish-rarity" -> Tag.preProcessParsed(rarity().displayName());
                case "sprite" -> Tag.preProcessParsed("<sprite:" + spriteAtlas() + ">");
                default -> null;
            };
        }

        return null;
    }

    private String spriteAtlas() {
        Material material = type.icon().itemStack().getType();
        if (material.isLegacy()) {
            throw new IllegalStateException("Legacy materials are not supported.");
        }
        else if (material.isBlock()) {
            return "\"minecraft:blocks\":block/" + material.key().value();
        }
        else if (material.isItem()) {
            return "\"minecraft:items\":item/" + material.key().value();
        }

        throw new IllegalStateException(material + " is not an block or item.");
    }

    @Override
    public boolean has(String name) {
        return name.equals("fish");
    }

    @Override
    public Component asComponent() {
        return (VirtualComponent) Argument.tagResolver(this);
    }
}
