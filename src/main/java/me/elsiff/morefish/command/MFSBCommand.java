package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.PREFIX_STRING;
import static me.elsiff.morefish.text.Lang.replace;

class MFSBCommand implements LiteralCommand {

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf scoreboard";
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return "Shows the competition scoreboard.";
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        Player player = (Player) context.getSource();
        if (getPlugin().getCompetition().isEnabled()) {
            MusiBoardHooker musiBoard = getPlugin().getMusiBoard();
            if (musiBoard.hasHooked()) {
                getPlugin().getMusiBoard().swapScoreboards(player);
                player.sendMessage(replace(PREFIX_STRING + "<white>Scoreboard swapped."));
                return 1;
            }

            player.sendMessage(replace(PREFIX_STRING + "<white>Scoreboard support is not enabled."));
            return 1;
        }

        player.sendMessage(replace(PREFIX_STRING + "<white>There is no competition running."));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "scoreboard";
    }
}
