package me.elsiff.morefish.hooker;

import me.elsiff.morefish.MoreFish;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProtocolLibHooker implements PluginHooker {

    @Nullable
    public SkullNbtHandler skullNbtHandler;
    private boolean hasHooked;

    @NotNull
    public String getPluginName() {
        return "ProtocolLib";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook(@NotNull MoreFish plugin) {
        PluginHooker.checkEnabled(this, plugin.getServer().getPluginManager());
        skullNbtHandler = new SkullNbtHandler();
        hasHooked = true;
    }

}
