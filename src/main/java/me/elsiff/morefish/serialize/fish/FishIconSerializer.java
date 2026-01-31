package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.FishIcon;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class FishIconSerializer implements TypeSerializer<FishIcon> {

    @Override
    public FishIcon deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new FishIcon(node.require(ItemStack.class));
    }

    @Override
    public void serialize(Type type, @Nullable FishIcon obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(obj.itemStack());
        }
    }
}
