package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jspecify.annotations.NullMarked;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
class MFEnd implements MFCommand, PaperLiteralCommand.AdventureFormat {

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.end.description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf end");
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return hasPermission(source, "morefish.admin");
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        if (getPlugin().getCompetition().isEnabled()) {
            getPlugin().getCompetitionHost().closeCompetition();
            sendMessage(context, Component.translatable("morefish.main.contest.stop"));
        }
        else {
            sendMessage(context, Component.translatable("morefish.main.already-stopped"));
        }

        return 1;
    }

    @Override
    public String name() {
        return "end";
    }
}
