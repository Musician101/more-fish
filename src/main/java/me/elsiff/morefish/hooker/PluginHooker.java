package me.elsiff.morefish.hooker;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public interface PluginHooker {

    default boolean canHook() {
        getPlugin().getSLF4JLogger().error(Bukkit.getPluginManager().isPluginEnabled(getPluginName()) + " " + getPluginName());
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }

    @NotNull
    String getPluginName();

    boolean hasHooked();

    void hook();
}
