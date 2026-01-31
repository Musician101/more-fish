package me.elsiff.morefish.hooker;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PluginHooker {

    default boolean canHook() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }

    String getPluginName();

    boolean hasHooked();

    void hook();
}
