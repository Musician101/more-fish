package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.PREFIX_COMPONENT;
import static me.elsiff.morefish.text.Lang.join;
import static net.kyori.adventure.text.Component.text;

class MFStart implements LiteralCommand {

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return "Start a competition.";
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf start [<seconds>]";
    }

    private static final Component ALREADY_ONGOING = join(PREFIX_COMPONENT, text("The contest is already ongoing."));

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFSeconds());
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    private static FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private static FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (getCompetition().isDisabled()) {
            getCompetitionHost().openCompetitionFor(getPlugin().getConfig().getInt("auto-running.timer") * 20L);
        }
        else {
            sender.sendMessage(ALREADY_ONGOING);
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
            if (getCompetition().isDisabled()) {
                long runningTime = context.getArgument("seconds", Long.class);
                getCompetitionHost().openCompetitionFor(runningTime * 20L);
            }
            else {
                sender.sendMessage(ALREADY_ONGOING);
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
