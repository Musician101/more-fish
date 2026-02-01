package me.elsiff.morefish.fish;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public record PlayerAnnouncement(PlayerAnnouncement.Type type, double radius) implements TagResolver {

    public PlayerAnnouncement {
        if (type == Type.RANGED) {
            Preconditions.checkArgument(radius >= 0, "Announcement radius must be a number greater than 0.");
        }
    }

    public List<Player> receiversOf(Player catcher) {
        return switch (type) {
            case NONE -> List.of();
            case SERVER -> new ArrayList<>(catcher.getServer().getOnlinePlayers());
            case PLAYER -> List.of(catcher);
            default ->
                    catcher.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(catcher.getLocation()) <= radius).toList();
        };
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            String key = arguments.popOr(name + " has missing argument").value();
            return switch (key) {
                case "type" -> Tag.preProcessParsed(type.toString().toLowerCase());
                case "radius" -> {
                    if (type == Type.RANGED) {
                        yield Tag.preProcessParsed(radius + "");
                    }

                    yield null;
                }
                default -> null;
            };
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("announcement");
    }

    public enum Type {
        NONE,
        PLAYER,
        RANGED,
        SERVER
    }
}
