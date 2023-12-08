package me.elsiff.morefish.common.hooker;

import org.jetbrains.annotations.NotNull;

public interface PluginHooker {

    static void checkEnabled(@NotNull PluginHooker hooker) {
        if (!hooker.canHook()) {
            throw new IllegalStateException(hooker.getPluginName() + " must be enabled.");
        }
    }

    static void checkHooked(@NotNull PluginHooker hooker) {
        if (!hooker.hasHooked()) {
            throw new IllegalStateException(hooker.getPluginName() + " must be hooked");
        }
    }

    boolean canHook();

    @NotNull String getPluginName();

    boolean hasHooked();

    void hook();

    default void hookIfEnabled() {
        if (canHook()) {
            hook();
        }
    }
}
