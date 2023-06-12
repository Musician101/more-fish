package me.elsiff.morefish.configuration;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TextReplacementConfig.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public interface Lang {

    Component PREFIX = text("[MoreFish] ", AQUA);
    Component CONTEST_START = join(PREFIX, text("The fishing contest has started!"));
    Component CONTEST_START_TIMER = join(PREFIX, text("This contest will end in %time%."));
    Component CONTEST_STOP = join(PREFIX, text("The fishing contest has ended!"));
    Component ALREADY_STOPPED = join(PREFIX, text("The contest is already stopped."));
    Component SHOP_DISABLED = join(PREFIX, text("Fish Shop is disabled now."));
    Component SHOP_GUI_TITLE = text("Put your fish to sell");

    @Nonnull
    static TextColor getColor(@Nonnull String string) {
        TextColor color = TextColor.fromHexString(string);
        return color == null ? NamedTextColor.NAMES.valueOr(string, WHITE) : WHITE;
    }

    private static FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    static Component join(ComponentLike... components) {
        return textOfChildren(components);
    }

    @Nonnull
    static Component replace(@Nonnull Component component, @Nonnull Map<String, Object> replacements, @Nullable Player player) {
        List<TextReplacementConfig> replacementConfigs = replacements.entrySet().stream().map(e -> {
            String key = e.getKey();
            String value = e.getValue().toString();
            Builder builder = TextReplacementConfig.builder().matchLiteral(key);
            if (key.equals("%rarity_color%")) {
                return builder.replacement(b -> b.content(b.content().replace("%rarity_color%", "")).color(getColor(value))).build();
            }

            return builder.replacement(value).build();
        }).collect(Collectors.toList());
        replacementConfigs.addAll(List.of(replaceTopPlayer(), replaceTopFishLength(), replaceTopFish(), replaceRank(player), replaceFish(player)));
        for (TextReplacementConfig replacementConfig : replacementConfigs) {
            component = component.replaceText(replacementConfig);
        }

        return component;
    }

    @Nonnull
    static Component replace(@Nonnull Component component, @Nonnull Map<String, Object> replacements) {
        return replace(component, replacements, null);
    }

    @Nonnull
    static List<Component> replace(@Nonnull List<Component> components, @Nonnull Map<String, Object> replacements, @Nullable Player player) {
        return components.stream().map(c -> replace(c, replacements, player)).toList();
    }

    private static TextReplacementConfig replaceFish(@Nullable Player player) {
        Builder builder = TextReplacementConfig.builder().matchLiteral("%rank%");
        if (player == null) {
            return builder.replacement("0").build();
        }

        return builder.replacement(getCompetition().containsContestant(player.getUniqueId()) ? String.valueOf(getCompetition().rankNumberOf(getCompetition().recordOf(player.getUniqueId()))) : "0").build();
    }

    private static TextReplacementConfig replaceRank(@Nullable Player player) {
        Builder builder = TextReplacementConfig.builder().matchLiteral("%fish%");
        if (player == null) {
            return builder.replacement("0").build();
        }

        return builder.replacement(getCompetition().containsContestant(player.getUniqueId()) ? String.valueOf(getCompetition().rankNumberOf(getCompetition().recordOf(player.getUniqueId()))) : "0").build();
    }

    private static TextReplacementConfig replaceTopFish() {
        return TextReplacementConfig.builder().match("%top_fish_([0-9]+)%").replacement((result, b) -> {
            String match = result.group();
            int number = Integer.parseInt(match.replace("%top_fish_", "").replaceAll("%", ""));
            return b.build().replaceText(b2 -> {
                b2.matchLiteral(match);
                b2.replacement(getCompetition().getRanking().size() >= number ? getCompetition().recordOf(number).fish().type().name() : "none");
            });
        }).build();
    }

    private static TextReplacementConfig replaceTopFishLength() {
        return TextReplacementConfig.builder().match("%top_fish_length_([0-9]+)%").replacement((result, b) -> {
            String match = result.group();
            int number = Integer.parseInt(match.replace("%top_fish_length_", "").replaceAll("%", ""));
            return b.build().replaceText(b2 -> {
                b2.matchLiteral(match);
                b2.replacement(getCompetition().getRanking().size() >= number ? String.valueOf(getCompetition().recordOf(number).fish().length()) : "0.0");
            });
        }).build();
    }

    private static TextReplacementConfig replaceTopPlayer() {
        return TextReplacementConfig.builder().match("%top_player_([0-9]+)%").replacement((result, b) -> {
            String match = result.group();
            int number = Integer.parseInt(match.replace("top_player_", "").replaceAll("%", ""));
            return b.build().replaceText(b2 -> {
                b2.matchLiteral(match);
                String name = Bukkit.getOfflinePlayer(getCompetition().recordOf(number).fisher()).getName();
                b2.replacement(getCompetition().getRanking().size() >= number ? (name != null ? name : "null") : "no one");
            });
        }).build();
    }

    @Nonnull
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
