package me.elsiff.morefish.serialize.record;

import me.elsiff.morefish.fish.FishIcon;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.serialize.fish.FishAbstractSerializer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class FishTypeRecordSerializer extends FishAbstractSerializer<FishType> {

    @Override
    public FishType deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishIcon icon = node.node("icon").require(FishIcon.class);
        FishType fishType = deserialize(node, (name, displayName) -> new FishType(name, new FishRarity("INVALID_RARITY", "INVALID RARITY"), displayName, icon));
        fishType.rarity(node.node("rarity").require(FishRarity.class));
        fishType.maxLength(node.node("length-max").require(Double.class));
        fishType.minLength(node.node("length-min").require(Double.class));
        return fishType;
    }

    @Override
    public void serialize(Type type, @Nullable FishType obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            super.serialize(type, obj, node);
            node.node("icon").set(obj.icon());
            node.node("rarity").set(obj.rarity());
            node.node("length-max").set(obj.maxLength());
            node.node("length-min").set(obj.minLength());
        }
    }
}
