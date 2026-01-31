package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperArgumentCommand;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.competition.FishingCompetition;
import me.elsiff.morefish.competition.FishingCompetitionHost;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
class MFStart implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private static final NodePath START_PATH = NodePath.path("command", "start");

    private void start(CommandSourceStack source, long runningTime) {
        if (getCompetition().isEnabled()) {
            sendMessage(source, lang().getComponent(START_PATH.withAppendedChild("on-going")));
        }
        else {
            getCompetitionHost().openCompetitionFor(runningTime * 20L);
        }
    }

    private FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return lang().getComponent(START_PATH.withAppendedChild("description"));
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf start [<seconds>]");
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new MFSeconds());
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return hasPermission(source, "morefish.admin");
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        start(context.getSource(), getPlugin().getConfig().getInt("auto-running.timer"));
        return 1;
    }

    @Override
    public String name() {
        return "start";
    }

    class MFSeconds implements PaperArgumentCommand.AdventureFormat<Long> {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            start(context.getSource(), context.getArgument("seconds", Long.class));
            return 1;
        }

        @Override
        public String name() {
            return "seconds";
        }

        @Override
        public LongArgumentType type() {
            return LongArgumentType.longArg(0);
        }
    }
}
