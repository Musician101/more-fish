package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MFMain implements LiteralCommand {

    @Override
    public boolean isRoot() {
        return true;
    }

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFClear(), new MFContraband(), new MFEnd(), new MFFishingLogs(), new MFGive(), new MFHelp(this), new MFLang(), new MFReload(), new MFScoreboard(), new MFSimulate(), new MFStart(), new MFShop(), new MFSuspend(), new MFTimes(), new MFTop());
    }

    @NotNull
    @Override
    public String name() {
        return "morefish";
    }
}
