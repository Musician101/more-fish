package me.elsiff.morefish.configuration;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public interface Lang {

    static Component join(ComponentLike... components) {
        return Component.join(JoinConfiguration.noSeparators(), components);
    }

    Component PREFIX = text("[MoreFish] ", AQUA);
    Component CATCH_FISH = join(PREFIX, text("%player%", YELLOW), text(" caught %rarity_color%%length%cm "), text("%fish_with_rarity%", Style.style(BOLD)));
    Component GET_1ST = join(PREFIX, text("%player%", YELLOW),  text(" is now the new 1st!"));
    Component NO_FISHING_ALLOWED = join(PREFIX, text("You can't fish unless the contest is ongoing."));
    Component CONTEST_START = join(PREFIX, text("The fishing contest has started!"));
    Component CONTEST_START_TIMER = join(PREFIX, text("This contest will end in %time%."));
    Component CONTEST_STOP = join(PREFIX, text("The fishing contest has ended!"));
    Component NO_PERMISSION = join(PREFIX, text("You don't have the permission."));
    Component ALREADY_ONGOING = join(PREFIX, text("The contest is already ongoing."));
    Component ALREADY_STOPPED = join(PREFIX, text("The contest is already stopped."));
    Component CLEAR_RECORDS = join(PREFIX, text("The records has been cleared successfully."));
    Component FAILED_TO_RELOAD = join(PREFIX, text("Failed to reload: Please check your console."));
    Component FORCED_PLAYER_TO_SHOP = join(PREFIX, text("Forced %s to open Shop GUI."));
    Component CREATED_SIGN_SHOP = join(PREFIX, text("You've created the Fish Shop!"));
    Component RELOAD_CONFIG = join(PREFIX, text("Reloaded the config successfully."));
    Component SHOP_DISABLED = join(PREFIX, text("Fish Shop is disabled now."));
    Component SHOP_GUI_TITLE = text("Put your fish to sell");
    Component SHOP_NO_FISH = join(PREFIX, text("There's no fish to sell. Please put them on the slots."));
    Component SHOP_SOLD = join(PREFIX, text("You sold fish for "), text("$%price%", GREEN), text("."));
    String TIMER_BOSS_BAR = ChatColor.AQUA + "" + ChatColor.BOLD + "Fishing Contest " + ChatColor.RESET + "[%time% left]";
    Component TIME_FORMAT_MINUTES = text("m");
    Component TIME_FORMAT_SECONDS = text("s");
    Component TOP_LIST = join(PREFIX, text("%ordinal%. ", YELLOW), text(": %player%, %length%cm %fish%", DARK_GRAY));
    Component TOP_MINE = join(PREFIX, text("You're %ordinal%: %length%cm %fish%"));
    Component TOP_MINE_NO_RECORD = join(PREFIX, text("You didn't get any record."));
    Component TOP_NO_RECORD = join(PREFIX, text("Nobody made any record yet."));

    private static FishingCompetition getCompetition() {
        return MoreFish.instance().getCompetition();
    }

    @Nonnull
    static Component replace(@Nonnull Component component, @Nonnull Map<String, Object> replacements, @Nullable Player player) {
        for (Entry<String, Object> replacement : replacements.entrySet()) {
            component = component.replaceText(builder -> {
                builder.matchLiteral(replacement.getKey());
                builder.replacement(replacement.getValue().toString());
            });
        }

        component = replaceTopPlayer(component);
        component = replaceTopFishLength(component);
        component = replaceTopFish(component);
        component = replaceRank(component, player);
        return replaceFish(component, player);
    }

    @Nonnull
    static String replace(@Nonnull String string, @Nonnull Map<String, Object> replacements, @Nullable Player player) {
        for (Entry<String, Object> replacement : replacements.entrySet()) {
            string = string.replaceAll(replacement.getKey(), replacement.getValue().toString());
        }

        string = replaceTopPlayer(string);
        string = replaceTopFishLength(string);
        string = replaceTopFish(string);
        string = replaceRank(string, player);
        return replaceFish(string, player);
    }

    @Nonnull
    static String replace(@Nonnull String string, @Nonnull Map<String, Object> replacements) {
        return replace(string, replacements, null);
    }

    @Nonnull
    static Component replace(@Nonnull Component component, @Nonnull Map<String, Object> replacements) {
        return replace(component, replacements, null);
    }

    @Nonnull
    static List<String> replace(@Nonnull List<String> strings, @Nonnull Map<String, Object> replacements, @Nullable Player player) {
        return strings.stream().map(s -> replace(s, replacements, player)).toList();
    }

    @Nonnull
    static List<Component> replaceComponents(@Nonnull List<Component> components, @Nonnull Map<String, Object> replacements, @Nullable Player player) {
        return components.stream().map(c -> replace(c, replacements, player)).toList();
    }

    private static Component replaceFish(@Nonnull Component component, @Nullable Player player) {
        if (player == null) {
            return component;
        }

        return component.replaceText(builder -> {
            builder.matchLiteral("%rank%");
            builder.replacement(getCompetition().containsContestant(player.getUniqueId()) ? getCompetition().recordOf(player.getUniqueId()).fish().type().name() : "0");
        });
    }

    private static String replaceFish(@Nonnull String string, @Nullable Player player) {
        if (player == null || !getCompetition().containsContestant(player.getUniqueId())) {
            return string;
        }

        return string.replaceAll("%fish%", getCompetition().recordOf(player.getUniqueId()).fish().type().name());
    }

    private static Component replaceRank(@Nonnull Component component, @Nullable Player player) {
        if (player == null) {
            return component;
        }

        return component.replaceText(builder -> {
            builder.matchLiteral("%fish%");
            builder.replacement(getCompetition().containsContestant(player.getUniqueId()) ? String.valueOf(getCompetition().rankNumberOf(getCompetition().recordOf(player.getUniqueId()))) : "0");
        });
    }

    private static String replaceRank(@Nonnull String string, @Nullable Player player) {
        if (player == null || !getCompetition().containsContestant(player.getUniqueId())) {
            return string;
        }

        return string.replaceAll("%rank%", String.valueOf(getCompetition().rankNumberOf(getCompetition().recordOf(player.getUniqueId()))));
    }

    private static Component replaceTopFish(@Nonnull Component component) {
        return component.replaceText(builder -> {
            builder.match("%top_fish_([0-9]+)%");
            builder.replacement((result, b) -> {
                String match = result.group();
                int number = Integer.parseInt(match.replace("%top_fish_", "").replaceAll("%", ""));
                return b.build().replaceText(b2 -> {
                    b2.matchLiteral(match);
                    b2.replacement(getCompetition().getRanking().size() >= number ? getCompetition().recordOf(number).fish().type().name() : "none");
                });
            });
        });
    }

    private static String replaceTopFish(@Nonnull String string) {
        Pattern pattern = Pattern.compile("%top_fish_([0-9]+)%");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            String match = matcher.group();
            int number = Integer.parseInt(match.replace("top_fish_", "").replaceAll("%", ""));
            if (getCompetition().getRanking().size() >= number) {
                return matcher.replaceAll(getCompetition().recordOf(number).fish().type().name());
            }

            return matcher.replaceAll("none");
        }

        return string;
    }

    private static Component replaceTopFishLength(@Nonnull Component component) {
        return component.replaceText(builder -> {
            builder.match("%top_fish_length_([0-9]+)%");
            builder.replacement((result, b) -> {
                String match = result.group();
                int number = Integer.parseInt(match.replace("%top_fish_length_", "").replaceAll("%", ""));
                return b.build().replaceText(b2 -> {
                    b2.matchLiteral(match);
                    b2.replacement(getCompetition().getRanking().size() >= number ? String.valueOf(getCompetition().recordOf(number).fish().length()) : "0.0");
                });
            });
        });
    }

    private static String replaceTopFishLength(@Nonnull String string) {
        Pattern pattern = Pattern.compile("%top_fish_length_([0-9]+)%");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            String match = matcher.group();
            int number = Integer.parseInt(match.replace("top_fish_length_", "").replaceAll("%", ""));
            if (getCompetition().getRanking().size() >= number) {
                return matcher.replaceAll(String.valueOf(getCompetition().recordOf(number).fish().length()));
            }

            return matcher.replaceAll("0.0");
        }

        return string;
    }

    private static Component replaceTopPlayer(@Nonnull Component component) {
        return component.replaceText(builder -> {
            builder.match("%top_player_([0-9]+)%");
            builder.replacement((result, b) -> {
                String match = result.group();
                int number = Integer.parseInt(match.replace("top_player_", "").replaceAll("%", ""));
                return b.build().replaceText(b2 -> {
                    b2.matchLiteral(match);
                    String name = Bukkit.getOfflinePlayer(getCompetition().recordOf(number).fisher()).getName();
                    b2.replacement(getCompetition().getRanking().size() >= number ? (name != null ? name : "null") : "no one");
                });
            });
        });
    }

    private static String replaceTopPlayer(@Nonnull String string) {
        Pattern pattern = Pattern.compile("%top_player_([0-9]+)%");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            String match = matcher.group();
            int number = Integer.parseInt(match.replace("top_player_", "").replaceAll("%", ""));
            if (getCompetition().getRanking().size() >= number) {
                return matcher.replaceAll(Bukkit.getOfflinePlayer(getCompetition().recordOf(number).fisher()).getName());
            }

            return matcher.replaceAll("no one");
        }

        return string;
    }

    @Nonnull
    static String time(long second) {
        StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofSeconds(second);
        if (duration.toMinutes() > 0L) {
            builder.append(duration.toMinutes()).append(Lang.TIME_FORMAT_MINUTES).append(" ");
        }

        builder.append(duration.getSeconds() % (long) 60).append(Lang.TIME_FORMAT_SECONDS);
        return builder.toString();
    }
}
