package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.musician101.bukkitier.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MFMain extends MFHelp {

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFClear(), new MFContraband(), new MFEnd(), new MFFLCommand(), new MFGive(), new MFHelp(), new MFReload(), new MFSBCommand(), new MFStart(), new MFShop(), new MFSuspend(), new MFTop());
    }

    @NotNull
    @Override
    public String name() {
        return "morefish";
    }
}
