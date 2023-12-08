package me.elsiff.morefish.paper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.musician101.bukkitier.command.Command;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MFMain extends MFHelp {

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFClear(), new MFContraband(), new MFEnd(), new MFGive(), new MFHelp(), new MFReload(), new MFSBCommand(), new MFStart(), new MFShop(), new MFSuspend(), new MFTop());
    }

    @NotNull
    @Override
    public String name() {
        return "morefish";
    }
}
