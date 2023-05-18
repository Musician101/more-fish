package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.musician101.bukkitier.command.Command;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;

public class MFMain extends MFHelp {

    @Nonnull
    @Override
    public String name() {
        return "morefish";
    }

    @Nonnull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFClear(), new MFContraband(), new MFEnd(), new MFGive(), new MFHelp(), new MFReload(), new MFStart(), new MFShop(), new MFSuspend(), new MFTop());
    }
}
