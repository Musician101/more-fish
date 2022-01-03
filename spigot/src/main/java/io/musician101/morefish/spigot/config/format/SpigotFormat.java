package io.musician101.morefish.spigot.config.format;

import io.musician101.morefish.common.config.format.Format;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;

public interface SpigotFormat<T extends SpigotFormat<T, R>, R> extends Format<T, R> {

    @Nonnull
    default String translated(@Nonnull String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @Nonnull
    default List<String> translated(@Nonnull List<String> strings) {
        return strings.stream().map(this::translated).collect(Collectors.toList());
    }
}
