package io.musician101.morefish.spigot.hooker;

import io.musician101.morefish.common.hooker.PluginHooker;
import org.bukkit.Bukkit;

public interface SpigotPluginHooker extends PluginHooker {

    @Override
    default boolean canHook() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }
}
