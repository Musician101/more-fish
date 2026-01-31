package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.FishIcon;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class FishTypeSerializer extends FishAbstractSerializer<FishType> {

    private final FishRarity rarity;

    public FishTypeSerializer(FishRarity rarity) {
        this.rarity = rarity;
    }

    @Override
    public FishType deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishIcon icon = node.node("icon").require(FishIcon.class);
        FishType fishType = deserialize(node, (name, displayName) -> new FishType(name, rarity, displayName, icon));
        fishType.maxLength(node.node("length-max").require(Double.class));
        fishType.minLength(node.node("length-min").require(Double.class));
        return fishType;
    }

    @Override
    public void serialize(Type type, @Nullable FishType obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            super.serialize(type, obj, node);
            node.node("icon").set(obj.icon());
            node.node("length-max").set(obj.maxLength());
            node.node("length-min").set(obj.minLength());
        }
    }
}
