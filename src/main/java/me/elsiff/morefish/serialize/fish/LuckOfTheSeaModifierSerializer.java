package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.LuckOfTheSeaModifier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.yaml.internal.snakeyaml.serializer.SerializerException;

import java.lang.reflect.Type;
import java.util.Arrays;

@NullMarked
public class LuckOfTheSeaModifierSerializer implements TypeSerializer<LuckOfTheSeaModifier> {

    @Override
    public LuckOfTheSeaModifier deserialize(Type type, ConfigurationNode node) throws SerializationException {
        LuckOfTheSeaModifier.Type modifierType = node.node("type").require(LuckOfTheSeaModifier.Type.class);
        float amount = node.node("amount").getFloat();
        return new LuckOfTheSeaModifier(modifierType, amount);
    }

    @Override
    public void serialize(Type type, @Nullable LuckOfTheSeaModifier obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.node("type").set(obj.type());
        node.node("amount").set(obj.amount());
    }

    public static class ModifierTypeSerializer implements TypeSerializer<LuckOfTheSeaModifier.Type> {

        @Override
        public LuckOfTheSeaModifier.Type deserialize(Type type, ConfigurationNode node) {
            return Arrays.stream(LuckOfTheSeaModifier.Type.values()).filter(t -> t.toString().equalsIgnoreCase(node.getString())).findFirst().orElseThrow(() -> new SerializerException(node.path() + " is either null or not a valid type. Must be 'flat' or 'percentage'"));
        }

        @Override
        public void serialize(Type type, LuckOfTheSeaModifier.@Nullable Type obj, ConfigurationNode node) throws SerializationException {
            node.set(obj == null ? null : obj.toString());
        }
    }
}
