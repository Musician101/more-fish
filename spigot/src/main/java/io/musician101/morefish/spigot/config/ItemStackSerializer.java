package io.musician101.morefish.spigot.config;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!type.equals(ItemStack.class)) {
            return null;
        }

        try {
            StringWriter sw = new StringWriter();
            YamlConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).build().save(node);
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.loadFromString(sw.toString());
            return ItemStack.deserialize(yaml.getValues(false));
        }
        catch (IOException | InvalidConfigurationException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            try {
                node.set(obj.serialize());
            }
            catch (ConfigurateException e) {
                throw new SerializationException(e);
            }
        }
    }
}
