package me.elsiff.morefish.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.competition.FishingCompetitionAutoRunner.CompetitionTimes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
class MFTimes implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private static final NodePath TIMES = NodePath.path("command", "times");

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm z");
        String times = Lists.partition(getPlugin().getAutoRunner().getCompetitionTimes(), 6).stream().map(list -> list.stream().map(CompetitionTimes::getStartTime).map(ZonedDateTime.now()::with).map(l -> l.format(formatter)).collect(Collectors.joining(", "))).collect(Collectors.joining("<newline>"));
        sendMessage(context, lang().getComponent(TIMES.withAppendedChild("message"), Placeholder.parsed("times", times)));
        return 1;
    }

    @Override
    public String name() {
        return "times";
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return lang().getComponent(TIMES.withAppendedChild("description"));
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf clear [alltime|competition [<player>]]");
    }
}
