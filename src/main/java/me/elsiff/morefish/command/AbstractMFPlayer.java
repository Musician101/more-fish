package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import io.musician101.bukkitier.command.ArgumentCommand;
import javax.annotation.Nonnull;
import me.elsiff.morefish.command.argument.PlayerArgumentType;
import org.bukkit.entity.Player;

public abstract class AbstractMFPlayer extends MFCommand implements ArgumentCommand<Player> {

    @Nonnull
    @Override
    public String name() {
        return "player";
    }

    @Nonnull
    @Override
    public ArgumentType<Player> type() {
        return new PlayerArgumentType();
    }
}
