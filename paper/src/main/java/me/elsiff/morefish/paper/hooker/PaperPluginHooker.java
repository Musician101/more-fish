package me.elsiff.morefish.paper.hooker;

import me.elsiff.morefish.common.hooker.PluginHooker;
import org.bukkit.Bukkit;

public abstract class PaperPluginHooker implements PluginHooker {

    protected boolean hasHooked;

    @Override
    public boolean canHook() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }

    @Override
    public boolean hasHooked() {
        return hasHooked;
    }
}
