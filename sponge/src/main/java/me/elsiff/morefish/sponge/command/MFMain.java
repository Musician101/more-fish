package me.elsiff.morefish.sponge.command;

import java.util.List;
import me.elsiff.morefish.sponge.SpongeMoreFish;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.plugin.PluginContainer;

public class MFMain extends MFHelp {

    public static void registerCommand(RegisterCommandEvent<Command> event) {
        PluginContainer pluginContainer = SpongeMoreFish.getPlugin().getPluginContainer();
        MFMain mfMain = new MFMain();
        event.register(pluginContainer, mfMain.toCommand(), mfMain.getName());
    }

    @Override
    public @NotNull List<MFCommand> getChildren() {
        return List.of(new MFClear(), new MFContraband(), new MFEnd(), new MFGive(), new MFHelp(), new MFReload(), new MFSBCommand(), new MFStart(), new MFShop(), new MFSuspend(), new MFTop());
    }

    @NotNull
    @Override
    public String getName() {
        return "morefish";
    }
}
