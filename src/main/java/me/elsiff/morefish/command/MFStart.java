package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.command.CommandSender;

class MFStart extends MFCommand implements LiteralCommand {

    @Override
    public int execute(@Nonnull CommandContext<CommandSender> context) {
        CommandSender sender = context.getSource();
        if (getCompetition().isDisabled()) {
            getCompetitionHost().openCompetitionFor(getConfig().getInt("auto-running.timer") * 20L);
            if (!getConfig().getBoolean("messages.broadcast-start", false)) {
                sender.sendMessage(Lang.CONTEST_START);
            }
        }
        else {
            sender.sendMessage(Lang.ALREADY_ONGOING);
        }

        return 1;
    }

    @Nonnull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFSeconds());
    }

    @Nonnull
    @Override
    public String name() {
        return "start";
    }

    @Override
    public boolean canUse(@Nonnull CommandSender sender) {
        return testAdmin(sender);
    }

    static class MFSeconds extends MFCommand implements ArgumentCommand<Long> {

        @Nonnull
        @Override
        public LongArgumentType type() {
            return LongArgumentType.longArg(0);
        }

        @Nonnull
        @Override
        public String name() {
            return "seconds";
        }

        @Override
        public int execute(@Nonnull CommandContext<CommandSender> context) {
            CommandSender sender = context.getSource();
            if (getCompetition().isDisabled()) {
                long runningTime = context.getArgument("seconds", Long.class);
                getCompetitionHost().openCompetitionFor(runningTime * 20L);
                if (!getConfig().getBoolean("messages.broadcast-start", false)) {
                    sender.sendMessage(Lang.replace(Lang.CONTEST_START_TIMER, Map.of("%time%", Lang.time(runningTime))));
                }
            }
            else {
                sender.sendMessage(Lang.ALREADY_ONGOING);
            }

            return 1;
        }
    }
}
