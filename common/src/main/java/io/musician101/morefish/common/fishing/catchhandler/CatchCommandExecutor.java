package io.musician101.morefish.common.fishing.catchhandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public abstract class CatchCommandExecutor implements CatchHandler {

    @Nonnull
    protected final List<String> commands;

    public CatchCommandExecutor(@Nonnull List<String> commands) {
        this.commands = commands;
    }

    public record Serializer(
            @Nonnull Function<List<String>, CatchCommandExecutor> mapper) implements TypeSerializer<CatchCommandExecutor> {

        @Override
        public CatchCommandExecutor deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (!type.equals(CatchCommandExecutor.class)) {
                return null;
            }

            return mapper.apply(node.getList(String.class, new ArrayList<>()));
        }

        @Override
        public void serialize(Type type, @Nullable CatchCommandExecutor obj, ConfigurationNode node) {

        }
    }
}
