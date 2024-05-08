package me.elsiff.morefish.hooker;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public interface PluginHooker {

    default boolean canHook() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }

    @NotNull
    String getPluginName();

    boolean hasHooked();

    void hook();

    default void hookIfEnabled() {
        if (canHook()) {
            hook();
            getPlugin().getSLF4JLogger().info("Successfully hooked into {}", getPluginName());
        }
    }
}
