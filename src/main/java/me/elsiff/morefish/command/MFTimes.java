package me.elsiff.morefish.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.competition.FishingCompetitionAutoRunner.CompetitionTimes;
import me.elsiff.morefish.text.Lang;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

class MFTimes implements LiteralCommand {

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm z");
        String times = Lists.partition(getPlugin().getAutoRunner().getCompetitionTimes(), 6).stream().map(list -> list.stream().map(CompetitionTimes::getStartTime).map(ZonedDateTime.now()::with).map(l -> l.format(formatter)).collect(Collectors.joining(", "))).collect(Collectors.joining("<newline>"));
        sender.sendMessage(Lang.replace("<mf-lang:command-times-message>", Lang.tagResolver("times", Lang.replace(times))));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "times";
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return Lang.raw("command-times-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf clear [alltime|competition [<player>]]";
    }
}
