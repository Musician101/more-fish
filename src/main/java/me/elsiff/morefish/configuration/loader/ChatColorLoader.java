package me.elsiff.morefish.configuration.loader;

import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public final class ChatColorLoader implements CustomLoader<ChatColor> {

    @Nonnull
    public ChatColor loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        return ChatColor.valueOf(section.getString(path).toUpperCase());
    }
}
