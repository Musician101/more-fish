package me.elsiff.morefish.text;

import me.elsiff.morefish.text.tagresolver.FishTagResolver;
import me.elsiff.morefish.text.tagresolver.RankTagResolver;
import me.elsiff.morefish.text.tagresolver.TopFishLengthTagResolver;
import me.elsiff.morefish.text.tagresolver.TopFishTagResolver;
import me.elsiff.morefish.text.tagresolver.TopPlayerTagResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public interface Lang {

    String PREFIX_STRING = "<dark_gray>[<gold>MoreFish<dark_gray>] ";
    Component PREFIX_COMPONENT = miniMessage().deserialize(PREFIX_STRING);
    Component CONTEST_START = join(PREFIX_COMPONENT, text("The fishing contest has started!"));
    Component CONTEST_STOP = join(PREFIX_COMPONENT, text("The fishing contest has ended!"));
    Component ALREADY_STOPPED = join(PREFIX_COMPONENT, text("The contest is already stopped."));
    Component SHOP_DISABLED = join(PREFIX_COMPONENT, text("Fish Shop is disabled now."));
    Component SHOP_GUI_TITLE = text("Put your fish to sell");

    static Component contestStartTimer(long time) {
        return join(PREFIX_COMPONENT, text("This contest will end in " + time(time) + "."));
    }

    static Component join(ComponentLike... components) {
        return textOfChildren(components);
    }

    @NotNull
    static Component replace(@NotNull String string, @NotNull Map<String, Object> replacements, @Nullable Player player) {
        for (Entry<String, Object> entry : replacements.entrySet()) {
            string = string.replaceAll(entry.getKey(), entry.getValue().toString());
        }

        UUID uuid = getUUID(player);
        return miniMessage().deserialize(string, new FishTagResolver(uuid), new RankTagResolver(uuid), new TopFishTagResolver(), new TopFishLengthTagResolver(), new TopPlayerTagResolver());
    }

    private static UUID getUUID(Player player) {
        return player == null ? null : player.getUniqueId();
    }

    @NotNull
    static Component replace(@NotNull String string) {
        return replace(string, Map.of());
    }

    @NotNull
    static Component replace(@NotNull String string, @NotNull Map<String, Object> replacements) {
        return replace(string, replacements, null);
    }

    @NotNull
    static List<Component> replace(@NotNull List<String> strings, @NotNull Map<String, Object> replacements, @Nullable Player player) {
        return strings.stream().map(c -> replace(c, replacements, player)).collect(Collectors.toList());
    }

    @NotNull
    static String time(long second) {
        StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofSeconds(second);
        if (duration.toMinutes() > 0L) {
            builder.append(duration.toMinutes()).append("m").append(" ");
        }

        builder.append(duration.getSeconds() % (long) 60).append("s");
        return builder.toString();
    }
}
