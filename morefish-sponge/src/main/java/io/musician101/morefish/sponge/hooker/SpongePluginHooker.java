package io.musician101.morefish.sponge.hooker;

import io.musician101.morefish.common.hooker.PluginHooker;
import org.spongepowered.api.Sponge;

public interface SpongePluginHooker extends PluginHooker {

    default boolean canHook() {
        return Sponge.getPluginManager().isLoaded(getPluginName());
    }
}
