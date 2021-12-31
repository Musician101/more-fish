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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public interface Lang {

    String PREFIX = ChatColor.AQUA + "[MoreFish] " + ChatColor.RESET;
    String CATCH_FISH = PREFIX + ChatColor.YELLOW + "%player%" + ChatColor.RESET + " caught %rarity_color%%length%cm " + ChatColor.BOLD + "%fish_with_rarity%";
    String GET_1ST = PREFIX + ChatColor.YELLOW + "%player%" + ChatColor.RESET + " is now the new 1st!";
    String NO_FISHING_ALLOWED = PREFIX + "You can't fish unless the contest is ongoing.";
    String CONTEST_START = PREFIX + "The fishing contest has started!";
    String CONTEST_START_TIMER = PREFIX + "This contest will end in %time%.";
    String CONTEST_STOP = PREFIX + "The fishing contest has ended!";
    String NO_PERMISSION = PREFIX + "You don't have the permission.";
    String ALREADY_ONGOING = PREFIX + "The contest is already ongoing.";
    String ALREADY_STOPPED = PREFIX + "The contest is already stopped.";
    String CLEAR_RECORDS = PREFIX + "The records has been cleared successfully.";
    String FAILED_TO_RELOAD = PREFIX + "Failed to reload: Please check your console.";
    String FORCED_PLAYER_TO_SHOP = PREFIX + "Forced %s to open Shop GUI.";
    String CREATED_SIGN_SHOP = PREFIX + "You've created the Fish Shop!";
    String RELOAD_CONFIG = PREFIX + "Reloaded the config successfully.";
    String SHOP_DISABLED = PREFIX + "Fish Shop is disabled now.";
    String SHOP_EMERALD_ICON_NAME = ChatColor.GREEN + "Sell for $%price%";
    String SHOP_GUI_TITLE = "Put your fish to sell";
    String SHOP_NO_FISH = PREFIX + "There's no fish to sell. Please put them on the slots.";
    String SHOP_SOLD = PREFIX + "You sold fish for " + ChatColor.GREEN + "$%price%" + ChatColor.RESET + ".";
    String TIMER_BOSS_BAR = ChatColor.AQUA + "" + ChatColor.BOLD + "Fishing Contest " + ChatColor.RESET + "[%time% left]";
    String TIME_FORMAT_MINUTES = "m";
    String TIME_FORMAT_SECONDS = "s";
    String TOP_LIST = PREFIX + ChatColor.YELLOW + "%ordinal%. " + ChatColor.DARK_GRAY + ": %player%, %length%cm %fish%";
    String TOP_MINE = PREFIX + "You're %ordinal%: %length%cm %fish%";
    String TOP_MINE_NO_RECORD = PREFIX + "You didn't get any record.";
    String TOP_NO_RECORD = PREFIX + "Nobody made any record yet.";

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
            string = string.replace(replacement.getKey(), replacement.getValue().toString());
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
            builder.replacement(getCompetition().containsContestant(player.getUniqueId()) ? getCompetition().recordOf(player.getUniqueId()).getFish().getType().getName() : "0");
        });
    }

    private static String replaceFish(@Nonnull String string, @Nullable Player player) {
        if (player == null) {
            return string;
        }

        if (getCompetition().containsContestant(player.getUniqueId())) {
            return string.replaceAll("%fish%", getCompetition().recordOf(player.getUniqueId()).getFish().getType().getName());
        }

        return "0";
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
        if (player == null) {
            return string;
        }

        if (getCompetition().containsContestant(player.getUniqueId())) {
            return string.replaceAll("%rank%", String.valueOf(getCompetition().rankNumberOf(getCompetition().recordOf(player.getUniqueId()))));
        }

        return "0";
    }

    private static Component replaceTopFish(@Nonnull Component component) {
        return component.replaceText(builder -> {
            builder.match("%top_fish_([0-9]+)%");
            builder.replacement((result, b) -> {
                String match = result.group();
                int number = Integer.parseInt(match.replace("%top_fish_", "").replaceAll("%", ""));
                return b.build().replaceText(b2 -> {
                    b2.matchLiteral(match);
                    b2.replacement(getCompetition().getRanking().size() >= number ? getCompetition().recordOf(number).getFish().getType().getName() : "none");
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
                return matcher.replaceAll(getCompetition().recordOf(number).getFish().getType().getName());
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
                    b2.replacement(getCompetition().getRanking().size() >= number ? String.valueOf(getCompetition().recordOf(number).getFish().getLength()) : "0.0");
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
                return matcher.replaceAll(String.valueOf(getCompetition().recordOf(number).getFish().getLength()));
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
                    String name = Bukkit.getOfflinePlayer(getCompetition().recordOf(number).getFisher()).getName();
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
                return matcher.replaceAll(Bukkit.getOfflinePlayer(getCompetition().recordOf(number).getFisher()).getName());
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
