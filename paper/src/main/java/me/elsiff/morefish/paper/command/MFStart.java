package me.elsiff.morefish.paper.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import java.util.List;
import java.util.Map;
import me.elsiff.morefish.common.configuration.Lang;
import me.elsiff.morefish.paper.configuration.PaperLang;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.common.configuration.Lang.CONTEST_START;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_START_TIMER;
import static net.kyori.adventure.text.Component.text;

class MFStart extends MFCommand implements LiteralCommand {

    private static final Component ALREADY_ONGOING = Lang.join(Lang.PREFIX, text("The contest is already ongoing."));

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFSeconds());
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return testAdmin(sender);
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (getCompetition().isDisabled()) {
            getCompetitionHost().openCompetitionFor(getConfig().getInt("auto-running.timer") * 20L);
            if (!getConfig().getBoolean("messages.broadcast-start", false)) {
                sender.sendMessage(CONTEST_START);
            }
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

    static class MFSeconds extends MFCommand implements ArgumentCommand<Long> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            if (getCompetition().isDisabled()) {
                long runningTime = context.getArgument("seconds", Long.class);
                getCompetitionHost().openCompetitionFor(runningTime * 20L);
                if (!getConfig().getBoolean("messages.broadcast-start", false)) {
                    sender.sendMessage(PaperLang.lang().replace(CONTEST_START_TIMER, Map.of("%time%", PaperLang.lang().time(runningTime))));
                }
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
