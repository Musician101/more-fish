package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.LuckOfTheSeaModifier;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import me.elsiff.morefish.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.yaml.internal.snakeyaml.serializer.SerializerException;

import java.lang.reflect.Type;

@NullMarked
public class LuckOfTheSeaModifierSerializer implements TypeSerializer<LuckOfTheSeaModifier> {

    private static final RequiredKey<LuckOfTheSeaModifier.Type> TYPE = ConfigKey.requiredKey("type", LuckOfTheSeaModifier.Type.class);
    private static final RequiredKey<Float> AMOUNT = ConfigKey.requiredKey("amount", Float.class);

    @Override
    public LuckOfTheSeaModifier deserialize(Type type, ConfigurationNode node) throws SerializationException {
        LuckOfTheSeaModifier.Type modifierType = TYPE.get(node);
        float amount = AMOUNT.get(node);
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
            return EnumUtils.getOrThrow(node.getString(), LuckOfTheSeaModifier.Type.class, new SerializerException(node.path() + " is either null or not a valid type. Must be 'flat' or 'percentage'"));
        }

        @Override
        public void serialize(Type type, LuckOfTheSeaModifier.@Nullable Type obj, ConfigurationNode node) throws SerializationException {
            node.set(obj == null ? null : obj.toString().toLowerCase());
        }
    }
}
