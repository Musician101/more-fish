package me.elsiff.morefish.serialize.record;

import me.elsiff.morefish.fish.FishIcon;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import me.elsiff.morefish.serialize.fish.FishAbstractSerializer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class FishTypeRecordSerializer extends FishAbstractSerializer<FishType> {

    private static final RequiredKey<FishIcon> ICON = ConfigKey.requiredKey("icon", FishIcon.class);
    private static final RequiredKey<FishRarity> RARITY = ConfigKey.requiredKey("rarity", FishRarity.class);
    private static final RequiredKey<Double> LENGTH_MAX = ConfigKey.requiredKey("length-max", Double.class);
    private static final RequiredKey<Double> LENGTH_MIN = ConfigKey.requiredKey("length-min", Double.class);

    @Override
    public FishType deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishIcon icon = ICON.get(node);
        FishType fishType = deserialize(node, (name, displayName) -> new FishType(name, new FishRarity("INVALID_RARITY", "INVALID RARITY"), displayName, icon));
        fishType.rarity(RARITY.get(node));
        fishType.maxLength(LENGTH_MAX.get(node));
        fishType.minLength(LENGTH_MIN.get(node));
        return fishType;
    }

    @Override
    public void serialize(Type type, @Nullable FishType obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            super.serialize(type, obj, node);
            ICON.set(node, obj.icon());
            RARITY.set(node, obj.rarity());
            LENGTH_MAX.set(node, obj.maxLength());
            LENGTH_MIN.set(node, obj.minLength());
        }
    }
}
