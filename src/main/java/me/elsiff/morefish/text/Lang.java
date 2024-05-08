package me.elsiff.morefish.text;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.text.tagresolver.FishTagResolver;
import me.elsiff.morefish.text.tagresolver.RankTagResolver;
import me.elsiff.morefish.text.tagresolver.TopFishLengthTagResolver;
import me.elsiff.morefish.text.tagresolver.TopFishTagResolver;
import me.elsiff.morefish.text.tagresolver.TopPlayerTagResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public interface Lang {

    String PREFIX_STRING = "<dark_gray>[<gold>MoreFish<dark_gray>] ";
    Component CONTEST_START = replace(PREFIX_STRING + "<white>The fishing contest has started!");
    Component CONTEST_STOP = replace(PREFIX_STRING + "<white>The fishing contest has ended!");
    Component ALREADY_STOPPED = replace(PREFIX_STRING + "<white>The contest is already stopped.");
    Component SHOP_DISABLED = replace(PREFIX_STRING + "<white>Fish Shop is disabled now.");
    Component SHOP_GUI_TITLE = text("Put your fish to sell");
    Component SALE_FILTERS_TITLE = text("Set Sale Filter(s)");

    static Component contestStartTimer(long time) {
        return replace(PREFIX_STRING + "<white>This contest will end in " + time(time) + ".");
    }

    @NotNull
    static Component replace(@NotNull String string, @NotNull List<TagResolver> tagResolvers, @Nullable Player player) {
        UUID uuid = player == null ? null : player.getUniqueId();
        List<TagResolver> list = new ArrayList<>(tagResolvers);
        list.addAll(List.of(new FishTagResolver(uuid), new RankTagResolver(uuid), new TopFishTagResolver(), new TopFishLengthTagResolver(), new TopPlayerTagResolver()));
        return miniMessage().deserialize(string, list.toArray(new TagResolver[0]));
    }

    @NotNull
    static TagResolver tagResolver(@NotNull String name, @NotNull String tag) {
        return tagResolver(name, text(tag));
    }

    @NotNull
    static TagResolver tagResolver(@NotNull String name, @NotNull Component tag) {
        return tagResolver(name, Tag.inserting(tag));
    }

    @SuppressWarnings("PatternValidation")
    @NotNull
    static TagResolver tagResolver(@NotNull String name, @NotNull Tag tag) {
        return TagResolver.builder().tag(name, tag).build();
    }

    @NotNull
    static TagResolver tagResolver(@NotNull String name, double tag) {
        return tagResolver(name, text(tag));
    }

    @NotNull
    static Component replace(@NotNull String string) {
        return replace(string, List.of());
    }

    @NotNull
    static Component replace(@NotNull String string, @NotNull List<TagResolver> tagResolvers) {
        return replace(string, tagResolvers, null);
    }

    @NotNull
    static List<Component> replace(@NotNull List<String> strings, @NotNull List<TagResolver> tagResolvers, @Nullable Player player) {
        return strings.stream().map(c -> replace(c, tagResolvers, player)).collect(Collectors.toList());
    }

    @NotNull
    static TagResolver playerName(@NotNull Player player) {
        return tagResolver("player", player.getName());
    }

    @NotNull
    static TagResolver fishLength(@NotNull Fish fish) {
        return tagResolver("length", fish.length());
    }

    @NotNull
    static TagResolver fishRarity(@NotNull Fish fish) {
        return tagResolver("rarity", fish.rarity().displayName().toUpperCase());
    }

    @NotNull
    static TagResolver fishRarityColor(@NotNull Fish fish) {
        return tagResolver("rarity_color", Tag.styling(builder -> {
            String color = fish.rarity().color();
            TextColor textColor = NamedTextColor.NAMES.valueOr(color, NamedTextColor.WHITE);
            if (color.startsWith(TextColor.HEX_PREFIX)) {
                textColor = TextColor.fromHexString(color);
            }

            builder.color(textColor);
        }));
    }

    @NotNull
    static TagResolver fishName(@NotNull Fish fish) {
        return tagResolver("fish", fish.name());
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
