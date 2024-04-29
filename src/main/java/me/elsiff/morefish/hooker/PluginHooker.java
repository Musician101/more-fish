package me.elsiff.morefish.hooker;

import me.elsiff.morefish.MoreFish;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public interface PluginHooker {

    static void checkEnabled(@NotNull PluginHooker hooker, @NotNull PluginManager pluginManager) {
        if (!hooker.canHook(pluginManager)) {
            throw new IllegalStateException(hooker.getPluginName() + " must be enabled.");
        }
    }

    static void checkHooked(@NotNull PluginHooker hooker) {
        if (!hooker.hasHooked()) {
            throw new IllegalStateException(hooker.getPluginName() + " must be hooked");
        }
    }

    default boolean canHook(@NotNull PluginManager pluginManager) {
        return pluginManager.isPluginEnabled(getPluginName());
    }

    @NotNull
    String getPluginName();

    boolean hasHooked();

    void hook(@NotNull MoreFish plugin);

    default void hookIfEnabled(@NotNull MoreFish plugin) {
        if (canHook(plugin.getServer().getPluginManager())) {
            hook(plugin);
        }
    }
}
