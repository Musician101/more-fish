package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.FishIcon;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class FishTypeSerializer extends FishAbstractSerializer<FishType> {

    private static final RequiredKey<FishIcon> ICON = ConfigKey.requiredKey("icon", FishIcon.class);
    private static final RequiredKey<Double> LENGTH_MAX = ConfigKey.requiredKey("length-max", Double.class);
    private static final RequiredKey<Double> LENGTH_MIN = ConfigKey.requiredKey("length-min", Double.class);

    @Override
    public FishType deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishIcon icon = ICON.get(node);
        FishRarity rarity = getPlugin().rarities().get(node.node("rarity").require(NamespacedKey.class)).orElseThrow(() -> new SerializationException(node.path() + " does not have a valid Rarity."));
        FishType fishType = deserialize(node, (key, displayName) -> new FishType(key, rarity, displayName, icon));
        fishType.maxLength(LENGTH_MAX.get(node));
        fishType.minLength(LENGTH_MIN.get(node));
        return fishType;
    }

    @Override
    public void serialize(Type type, @Nullable FishType obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            super.serialize(type, obj, node);
            ICON.set(node, obj.icon());
            LENGTH_MAX.set(node, obj.maxLength());
            LENGTH_MIN.set(node, obj.minLength());
        }
    }
}
