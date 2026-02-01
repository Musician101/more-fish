package me.elsiff.morefish.serialize.fish.condition;

import io.papermc.paper.registry.RegistryKey;
import me.elsiff.morefish.fish.condition.BiomesCondition;
import org.bukkit.block.Biome;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@NullMarked
public class BiomeConditionSerializer extends FishConditionSerializer<BiomesCondition> {

    @Override
    public BiomesCondition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        List<Biome> biomes = node.getList(String.class, List.of()).stream().map(s -> loadFromRegistry(RegistryKey.BIOME, s)).filter(Objects::nonNull).toList();
        return new BiomesCondition(biomes);
    }

    @Override
    public void serialize(Type type, @Nullable BiomesCondition obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(obj.value().stream().map(this::saveFromRegistry).toList());
        }
    }
}
