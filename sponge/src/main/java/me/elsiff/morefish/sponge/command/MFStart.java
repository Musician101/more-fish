package me.elsiff.morefish.sponge.command;

import java.util.Map;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import static me.elsiff.morefish.common.configuration.Lang.CONTEST_START;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_START_TIMER;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.lang;
import static net.kyori.adventure.text.Component.text;

class MFStart extends MFCommand {

    private static final Component ALREADY_ONGOING = join(PREFIX, text("The contest is already ongoing."));
    private final Parameter.Value<Integer> seconds = Parameter.rangedInteger(0, Integer.MAX_VALUE).key("seconds").build();

    @Override
    public boolean canUse(@NotNull CommandContext context) {
        return testAdmin(context);
    }

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        if (getCompetition().isDisabled()) {
            Optional<Integer> optional = context.one(this.seconds);
            int seconds = optional.orElse(getConfig().node("auto-running.timer").getInt());
            getCompetitionHost().openCompetitionFor(seconds * 20L);
            if (!getConfig().node("messages.broadcast-start").getBoolean(false)) {
                if (optional.isPresent()) {
                    context.sendMessage(lang().replace(CONTEST_START_TIMER, Map.of("%time%", lang().time(seconds))));
                }
                else {
                    context.sendMessage(CONTEST_START);
                }
            }
        }
        else {
            context.sendMessage(ALREADY_ONGOING);
        }

        return CommandResult.success();
    }

    @NotNull
    @Override
    public String getName() {
        return "start";
    }
}
