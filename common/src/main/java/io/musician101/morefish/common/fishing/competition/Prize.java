package io.musician101.morefish.common.fishing.competition;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public abstract class Prize {

    @Nonnull
    protected final List<String> commands;

    protected Prize(@Nonnull List<String> commands) {
        this.commands = commands;
    }

    public abstract void giveTo(@Nonnull UUID user, int rankNumber);

    public record Serializer(@Nonnull Function<List<String>, Prize> prizeMapper) implements TypeSerializer<Prize> {

        @Override
        public Prize deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (!type.equals(Prize.class)) {
                return null;
            }

            return prizeMapper.apply(node.getList(String.class));
        }

        @Override
        public void serialize(Type type, @Nullable Prize obj, ConfigurationNode node) {

        }
    }
}
