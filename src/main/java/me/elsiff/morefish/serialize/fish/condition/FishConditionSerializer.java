package me.elsiff.morefish.serialize.fish.condition;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.elsiff.morefish.fish.condition.FishCondition;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public abstract class FishConditionSerializer<C extends FishCondition<?>> implements TypeSerializer<C> {

    @SuppressWarnings("PatternValidation")
    @Nullable
    protected <V extends Keyed> V loadFromRegistry(RegistryKey<V> registryKey, String key) {
        Key k = Key.key(key);
        Registry<V> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
        return registry.get(k);
    }

    protected <K> Map<K, Integer> loadMappedConditions(ConfigurationNode node, Function<String, @Nullable K> keyMapper) {
        return node.childrenMap().entrySet().stream().map(e -> {
            K keyValue = keyMapper.apply(e.getKey().toString());
            if (keyValue == null) {
                return null;
            }

            return new SimpleEntry<>(keyValue, e.getValue().getInt());
        }).filter(Objects::nonNull).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    protected <K, V> void saveMappedCondition(ConfigurationNode node, Map<K, V> map, Function<K, String> keyMapper) throws SerializationException {
        for (Entry<K, V> entry : map.entrySet()) {
            node.node(keyMapper.apply(entry.getKey())).set(entry.getValue());
        }
    }

    protected <K extends Keyed> String saveFromRegistry(K keyed) {
        return keyed.getKey().asString();
    }
}
