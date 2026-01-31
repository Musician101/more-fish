package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.ComponentLike;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class MFMain implements PaperLiteralCommand.AdventureFormat {

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new MFClear(), new MFContraband(), new MFEdit(), new MFEnd(), new MFFishingLogs(), new MFGive(), new MFHelp(this), new MFReload(), new MFScoreboard(), new MFSimulate(), new MFStart(), new MFShop(), new MFSuspend(), new MFTimes(), new MFTop());
    }

    @Override
    public String name() {
        return "morefish";
    }
}
