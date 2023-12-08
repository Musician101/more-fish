package me.elsiff.morefish.paper.hooker;

import me.elsiff.morefish.common.hooker.PluginHooker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProtocolLibHooker extends PaperPluginHooker {

    @Nullable public SkullNbtHandler skullNbtHandler;

    @NotNull
    @Override
    public String getPluginName() {
        return "ProtocolLib";
    }

    @Override
    public void hook() {
        PluginHooker.checkEnabled(this);
        skullNbtHandler = new SkullNbtHandler();
        hasHooked = true;
    }

}
