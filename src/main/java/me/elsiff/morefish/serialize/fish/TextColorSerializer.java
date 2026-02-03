package me.elsiff.morefish.serialize.fish;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class TextColorSerializer implements TypeSerializer<TextColor> {

    @Override
    public @Nullable TextColor deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String string = node.require(String.class);
        TextColor color = NamedTextColor.NAMES.valueOr(string, NamedTextColor.WHITE);
        if (string.startsWith("#")) {
            color = TextColor.fromHexString(string);
        }

        return color;
    }

    @Override
    public void serialize(Type type, @Nullable TextColor obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            throw new SerializationException("Color cannot be null.");
        }

        if (obj instanceof NamedTextColor named) {
            node.set(NamedTextColor.NAMES.key(named));
            return;
        }

        node.set(obj.asHexString());
    }
}
