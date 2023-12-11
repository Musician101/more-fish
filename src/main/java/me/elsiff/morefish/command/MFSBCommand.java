package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;

public class MFSBCommand extends MFCommand implements LiteralCommand {

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return "Show the scoreboard for the current competition";
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) throws CommandSyntaxException {
        Player player = (Player) context.getSource();
        if (getCompetition().isEnabled()) {
            MusiBoardHooker musiBoard = getPlugin().getMusiBoard();
            if (musiBoard.hasHooked()) {
                getPlugin().getMusiBoard().swapScoreboards(player);
                player.sendMessage(join(Lang.PREFIX, text("Scoreboard swapped.")));
                return 1;
            }

            player.sendMessage(join(Lang.PREFIX, text("Scoreboard support is not enabled.")));
            return 1;
        }

        player.sendMessage(join(Lang.PREFIX, text("There is no competition running.")));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "scoreboard";
    }
}