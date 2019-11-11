package me.elsiff.morefish.hooker;

import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.plugin.PluginManager;

public interface PluginHooker {

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

    void setHasHooked(boolean var1);

    final class Companion {

        private Companion() {
        }

        public static void checkEnabled(@Nonnull PluginHooker hooker, @Nonnull PluginManager pluginManager) {
            if (!hooker.canHook(pluginManager)) {
                throw new IllegalStateException(hooker.getPluginName() + " must be enabled.");
            }
        }

        public static void checkHooked(@Nonnull PluginHooker hooker) {
            if (!hooker.hasHooked()) {
                throw new IllegalStateException(hooker.getPluginName() + " must be hooked");
            }

        }
    }
}
