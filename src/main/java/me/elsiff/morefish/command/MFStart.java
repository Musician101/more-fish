package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.text.Lang;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

class MFStart implements LiteralCommand {

    private static void start(CommandSender sender, long runningTime) {
        if (getCompetition().isEnabled()) {
            sender.sendMessage(Lang.replace("<mf-lang:command-start-ongoing>"));
        }
        else {
            getCompetitionHost().openCompetitionFor(runningTime * 20L);
        }
    }

    private static FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return Lang.raw("command-start-description");
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
        start(context.getSource(), getPlugin().getConfig().getInt("auto-running.timer"));
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
            start(context.getSource(), context.getArgument("seconds", Long.class));
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
