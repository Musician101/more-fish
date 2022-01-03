package io.musician101.morefish.common.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

public abstract class CatchFireworkSpawner implements CatchHandler {

    @Override
    public final void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        if (fish.getType().hasCatchFirework()) {
            spawnFirework(catcherID);
        }
    }

    protected abstract void spawnFirework(@Nonnull UUID uuid);

    public record Serializer(
            @Nonnull Supplier<CatchFireworkSpawner> supplier) implements TypeSerializer<CatchFireworkSpawner> {

        @Override
        public CatchFireworkSpawner deserialize(Type type, ConfigurationNode node) {
            if (!type.equals(CatchFireworkSpawner.class)) {
                return null;
            }

            return node.getBoolean() ? supplier.get() : null;
        }

        @Override
        public void serialize(Type type, @Nullable CatchFireworkSpawner obj, ConfigurationNode node) {

        }
    }
}
