package me.elsiff.morefish.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.competition.FishingCompetitionAutoRunner.CompetitionTimes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.jspecify.annotations.NullMarked;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
class MFTimes implements MFCommand, PaperLiteralCommand.AdventureFormat {

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm z");
        String times = Lists.partition(getPlugin().getAutoRunner().getCompetitionTimes(), 6).stream().map(list -> asString(list, formatter)).collect(Collectors.joining("<newline>"));
        sendMessage(context, Component.translatable("morefish.command.times.message", Argument.component("times", MiniMessage.miniMessage().deserialize(times))));
        return 1;
    }

    private String asString(List<CompetitionTimes> times, DateTimeFormatter formatter) {
        return times.stream().map(CompetitionTimes::getStartTime).map(ZonedDateTime.now()::with).map(l -> l.format(formatter)).collect(Collectors.joining(", "));
    }

    @Override
    public String name() {
        return "times";
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.times.description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf clear [alltime|competition [<player>]]");
    }
}
