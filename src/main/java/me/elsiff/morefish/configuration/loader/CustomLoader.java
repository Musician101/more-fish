package me.elsiff.morefish.configuration.loader;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.configuration.ConfigurationSection;

public interface CustomLoader<T> {

    String ROOT_PATH = "";

    @Nonnull
    T loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path);

    @Nullable
    default Optional<T> loadIfExists(@Nonnull ConfigurationSection section, @Nonnull String path) {
        if (section.contains(path)) {
            return Optional.of(loadFrom(section, path));
        }
        else {
            return Optional.empty();
        }
    }
}
