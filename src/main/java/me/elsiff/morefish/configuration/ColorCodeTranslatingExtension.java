package me.elsiff.morefish.configuration;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;

public final class ColorCodeTranslatingExtension {

    @Nonnull
    public static String translated(@Nonnull String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @Nonnull
    public static List<String> translated(@Nonnull List<String> strings) {
        return strings.stream().map(ColorCodeTranslatingExtension::translated).collect(Collectors.toList());
    }
}
