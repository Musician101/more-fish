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
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record LuckOfTheSeaModifier(Type type, float amount) implements ComponentLike, TagResolver {

    public static final LuckOfTheSeaModifier NONE = new LuckOfTheSeaModifier(Type.FLAT, 0);

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            return null;
        }

        String value = arguments.popOr(name + " needs at least 1 argument").value();
        return switch (value) {
            case "type" ->
                    Tag.selfClosingInserting(Component.translatable("morefish.editor.rarity.selected.luck-of-the-sea-modifier.modifier-type", Argument.string("modifier-type", type.toString())));
            case "amount" -> TagResolverUtil.numberTag(value, amount, arguments, ctx);
            default -> null;
        };
    }

    @Override
    public boolean has(String name) {
        return name.equalsIgnoreCase("luck-of-the-sea-modifier");
    }

    @Override
    public Component asComponent() {
        return (VirtualComponent) Argument.tagResolver(this);
    }

    public enum Type {
        FLAT,
        PERCENTAGE
    }
}
