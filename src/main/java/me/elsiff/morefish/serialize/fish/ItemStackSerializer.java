package me.elsiff.morefish.serialize.fish;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@NullMarked
public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Map<String, Object> map = new HashMap<>();
        for (Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
            // In theory, ConfigurationNode#get() will never be null.
            //noinspection DataFlowIssue
            map.put(entry.getKey().toString(), entry.getValue().get(Object.class));
        }

        return ItemStack.deserialize(map);
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        node.set(obj == null ? null : obj.serialize());
    }
}
