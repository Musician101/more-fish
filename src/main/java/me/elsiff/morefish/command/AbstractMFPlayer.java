package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import io.musician101.bukkitier.command.ArgumentCommand;
import me.elsiff.morefish.command.argument.PlayerArgumentType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMFPlayer implements ArgumentCommand<Player> {

    @NotNull
    @Override
    public String name() {
        return "player";
    }

    @NotNull
    @Override
    public ArgumentType<Player> type() {
        return new PlayerArgumentType();
    }
}
