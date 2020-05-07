package io.musician101.morefish.common.hooker;

import javax.annotation.Nonnull;

public interface PluginHooker {

    static void checkEnabled(@Nonnull PluginHooker hooker) {
        if (!hooker.canHook()) {
            throw new IllegalStateException(hooker.getPluginName() + " must be enabled.");
        }
    }

    static void checkHooked(@Nonnull PluginHooker hooker) {
        if (!hooker.hasHooked()) {
            throw new IllegalStateException(hooker.getPluginName() + " must be hooked");
        }
    }

    boolean canHook();

    @Nonnull
    String getPluginName();

    boolean hasHooked();

    void hook();

    default void hookIfEnabled() {
        if (canHook()) {
            hook();
        }
    }

    void setHasHooked(boolean var1);
}
