package io.musician101.morefish.common.fishing.condition;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public interface FishCondition {

    boolean check(@Nonnull UUID item, @Nonnull UUID player);

    record Serializer(@Nonnull FishConditionManager fishConditionManager) implements TypeSerializer<FishCondition> {

        @SuppressWarnings("ConstantConditions")
        @Override
        public FishCondition deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (!type.equals(FishCondition.class)) {
                return null;
            }

            List<String> tokens = new ArrayList<>(Arrays.asList(node.getString().split("\\|")));
            String id = tokens.get(0);
            tokens.remove(0);
            String[] args = tokens.toArray(new String[0]);
            return fishConditionManager.getFishCondition(id, args).orElseThrow(() -> new SerializationException("There's no fish condition whose id is " + id));
        }

        @Override
        public void serialize(Type type, @Nullable FishCondition obj, ConfigurationNode node) {

        }
    }
}
