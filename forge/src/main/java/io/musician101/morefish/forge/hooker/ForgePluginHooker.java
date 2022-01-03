package io.musician101.morefish.forge.hooker;

import io.musician101.morefish.common.hooker.PluginHooker;

public interface ForgePluginHooker extends PluginHooker {

    @Override
    default boolean canHook() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }
}
