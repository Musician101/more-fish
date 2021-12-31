package me.elsiff.morefish.hooker;

import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.plugin.PluginManager;

public interface PluginHooker {

    static void checkEnabled(@Nonnull PluginHooker hooker, @Nonnull PluginManager pluginManager) {
        if (!hooker.canHook(pluginManager)) {
            throw new IllegalStateException(hooker.getPluginName() + " must be enabled.");
        }
    }

    static void checkHooked(@Nonnull PluginHooker hooker) {
        if (!hooker.hasHooked()) {
            throw new IllegalStateException(hooker.getPluginName() + " must be hooked");
        }
    }

    default boolean canHook(@Nonnull PluginManager pluginManager) {
        return pluginManager.isPluginEnabled(getPluginName());
    }

    @Nonnull
    String getPluginName();

    boolean hasHooked();

    void hook(@Nonnull MoreFish plugin);

    default void hookIfEnabled(@Nonnull MoreFish plugin) {
        if (canHook(plugin.getServer().getPluginManager())) {
            hook(plugin);
        }
    }
}
