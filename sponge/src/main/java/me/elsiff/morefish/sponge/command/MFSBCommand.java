package me.elsiff.morefish.sponge.command;

import me.elsiff.morefish.sponge.hooker.ScoreboardHooker;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import static me.elsiff.morefish.common.configuration.Lang.PREFIX;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class MFSBCommand extends MFCommand {

    @Override
    public boolean canUse(@NotNull CommandCause cause) {
        return cause instanceof ServerPlayer;
    }

    @Override
    public @NotNull Component description(CommandCause cause) {
        return text("Toggles your scoreboard between your active and the fishing competition's boards.", GRAY);
    }

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        ServerPlayer player = (ServerPlayer) context.cause();
        if (getCompetition().isEnabled()) {
            ScoreboardHooker musiBoard = getPlugin().getScoreboardHooker();
            if (musiBoard.hasHooked()) {
                getPlugin().getScoreboardHooker().swapScoreboards(player);
                player.sendMessage(join(PREFIX, text("Scoreboard swapped.")));
                return CommandResult.success();
            }

            player.sendMessage(join(PREFIX, text("Scoreboard support is not enabled.")));
            return CommandResult.success();
        }

        player.sendMessage(join(PREFIX, text("There is no competition running.")));
        return CommandResult.success();
    }

    @NotNull
    @Override
    public String getName() {
        return "scoreboard";
    }

    @Override
    public @NotNull Component usage(CommandCause commandCause) {
        return text("/mf scoreboard");
    }
}
