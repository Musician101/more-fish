package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
class MFScoreboard implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private final static NodePath SCOREBOARD_PATH = NodePath.path("command", "scoreboard");

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf scoreboard");
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return isPlayer(source);
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return lang().getComponent(SCOREBOARD_PATH.withAppendedChild("description"));
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        if (getPlugin().getCompetition().isEnabled()) {
            MusiBoardHooker musiBoard = getPlugin().getMusiBoard();
            if (musiBoard.hasHooked()) {
                getPlugin().getMusiBoard().swapScoreboards(player);
                player.sendMessage(lang().getComponent(SCOREBOARD_PATH.withAppendedChild("success")));
                return 1;
            }

            player.sendMessage(lang().getComponent(SCOREBOARD_PATH.withAppendedChild("no-support")));
            return 1;
        }

        player.sendMessage(lang().getComponent(SCOREBOARD_PATH.withAppendedChild("no-competition")));
        return 1;
    }

    @Override
    public String name() {
        return "scoreboard";
    }
}
