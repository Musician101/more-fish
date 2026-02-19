package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jspecify.annotations.NullMarked;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class MFSuspend implements MFCommand, PaperLiteralCommand.AdventureFormat {

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.suspend.description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf suspend");
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return hasPermission(source, "morefish.admin");
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (getPlugin().getCompetition().isEnabled()) {
            getPlugin().getCompetitionHost().closeCompetition(true);
            sendMessage(source, Component.translatable("morefish.main.contest.stop"));
        }
        else {
            sendMessage(source, Component.translatable("morefish.main.already-stopped"));
        }

        return 1;
    }

    @Override
    public String name() {
        return "suspend";
    }
}
