package me.elsiff.morefish.sponge.hooker;

import me.elsiff.morefish.common.hooker.PluginHooker;
import org.spongepowered.api.Sponge;

public abstract class SpongePluginHooker implements PluginHooker {

    protected boolean hasHooked;

    @Override
    public boolean canHook() {
        return Sponge.pluginManager().plugin(getPluginName()).isPresent();
    }

    @Override
    public boolean hasHooked() {
        return hasHooked;
    }
}
