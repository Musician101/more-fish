package me.elsiff.morefish.sponge.command;

import io.musician101.spongecmd.CMDExecutor;
import io.musician101.spongecmd.help.HelpMainCMD;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public class MFMain extends HelpMainCMD {

    public MFMain() {
        super(getPlugin().getPluginContainer());
    }

    @Override
    public List<String> getAliases() {
        return List.of("mf");
    }

    @Override
    public @NotNull List<CMDExecutor> getChildren() {
        return List.of(new MFClear(), new MFContraband(), new MFEnd(), new MFGive(), new MFHelp(this), new MFReload(), new MFSBCommand(), new MFStart(), new MFShop(), new MFSuspend(), new MFTop());
    }

    @NotNull
    @Override
    public String getName() {
        return "morefish";
    }
}
