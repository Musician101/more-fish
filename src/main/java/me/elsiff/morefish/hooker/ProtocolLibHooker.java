package me.elsiff.morefish.hooker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.MoreFish;

public final class ProtocolLibHooker implements PluginHooker {

    @Nullable
    public SkullNbtHandler skullNbtHandler;
    private boolean hasHooked;

    @Nonnull
    public String getPluginName() {
        return "ProtocolLib";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook(@Nonnull MoreFish plugin) {
        PluginHooker.Companion.checkEnabled(this, plugin.getServer().getPluginManager());
        skullNbtHandler = new SkullNbtHandler();
        hasHooked = true;
    }

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }
}
