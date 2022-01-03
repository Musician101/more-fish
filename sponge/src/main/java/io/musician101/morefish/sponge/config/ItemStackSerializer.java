package io.musician101.morefish.sponge.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.StringDataFormat;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    private final StringDataFormat dataFormat = DataFormats.HOCON.get();

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!type.equals(ItemStack.class)) {
            return null;
        }

        try {
            StringWriter sw = new StringWriter();
            HoconConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).build().save(node);
            return ItemStack.builder().fromContainer(dataFormat.read(sw.toString())).build();
        }
        catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            try {
                node.set(HoconConfigurationLoader.builder().source(() -> new BufferedReader(new StringReader(dataFormat.write(obj.toContainer())))).build().load());
            }
            catch (ConfigurateException e) {
                throw new SerializationException(e);
            }
        }
    }
}
