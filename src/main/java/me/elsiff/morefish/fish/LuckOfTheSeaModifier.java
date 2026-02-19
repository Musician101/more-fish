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
public class LuckOfTheSeaModifier implements ComponentLike, TagResolver {

    public static final LuckOfTheSeaModifier NONE = new LuckOfTheSeaModifier(Type.FLAT, 0);
    private final Type type;
    private final float amount;

    public LuckOfTheSeaModifier(Type type, float amount) {
        this.type = type;
        this.amount = amount;
    }

    public Type type() {
        return type;
    }

    public float amount() {
        return amount;
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            return null;
        }

        String value = arguments.popOr(name + " needs at least 1 argument").value();
        return switch (value) {
            case "type" -> Tag.preProcessParsed(type.toString());
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
