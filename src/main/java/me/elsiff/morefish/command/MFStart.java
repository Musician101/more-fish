package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.raw;
import static me.elsiff.morefish.text.Lang.replace;

class MFStart implements LiteralCommand {

    private static FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return raw("command-start-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf start [<seconds>]";
    }

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFSeconds());
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (getCompetition().isEnabled()) {
            sender.sendMessage(replace("<mf-lang:command-start-ongoing>"));
        }
        else {
            getCompetitionHost().openCompetitionFor(getPlugin().getConfig().getInt("auto-running.timer") * 20L);
        }

        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "start";
    }

    static class MFSeconds implements ArgumentCommand<Long> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            if (getCompetition().isEnabled()) {
                sender.sendMessage(replace("<mf-lang:command-start-ongoing>"));
            }
            else {
                long runningTime = context.getArgument("seconds", Long.class);
                getCompetitionHost().openCompetitionFor(runningTime * 20L);
            }

            return 1;
        }

        @NotNull
        @Override
        public String name() {
            return "seconds";
        }

        @NotNull
        @Override
        public LongArgumentType type() {
            return LongArgumentType.longArg(0);
        }
    }
}
