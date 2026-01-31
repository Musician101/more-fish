package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MFCommand {

    default boolean hasPermission(CommandSourceStack source, String permission) {
        return source.getSender().hasPermission(permission);
    }

    default void sendMessage(CommandContext<CommandSourceStack> context, Component message) {
        sendMessage(context.getSource(), message);
    }

    default void sendMessage(CommandSourceStack source, Component message) {
        source.getSender().sendMessage(message);
    }

    default Player getPlayer(CommandContext<CommandSourceStack> context) {
        return (Player) context.getSource().getSender();
    }

    default boolean isPlayer(CommandSourceStack source) {
        return source.getSender() instanceof Player;
    }

    default boolean isPlayerAndHasPermission(CommandSourceStack source, String permission) {
        return hasPermission(source, permission) && isPlayer(source);
    }
}
