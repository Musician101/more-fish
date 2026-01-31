package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import io.musician101.musicommand.paper.command.PaperArgumentCommand;
import me.elsiff.morefish.command.argument.PlayerArgumentType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractMFPlayer implements MFCommand, PaperArgumentCommand.AdventureFormat<Player> {

    @Override
    public String name() {
        return "player";
    }

    @Override
    public ArgumentType<Player> type() {
        return new PlayerArgumentType();
    }
}
