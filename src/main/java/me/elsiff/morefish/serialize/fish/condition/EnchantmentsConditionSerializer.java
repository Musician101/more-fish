package me.elsiff.morefish.serialize.fish.condition;

import io.papermc.paper.registry.RegistryKey;
import me.elsiff.morefish.fish.condition.EnchantmentsCondition;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public class EnchantmentsConditionSerializer extends FishConditionSerializer<EnchantmentsCondition> {

    @Override
    public EnchantmentsCondition deserialize(Type type, ConfigurationNode node) {
        return new EnchantmentsCondition(loadMappedConditions(node, s -> loadFromRegistry(RegistryKey.ENCHANTMENT, s)));
    }

    @Override
    public void serialize(Type type, @Nullable EnchantmentsCondition obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            saveMappedCondition(node, obj.value(), this::saveFromRegistry);
        }
    }
}
