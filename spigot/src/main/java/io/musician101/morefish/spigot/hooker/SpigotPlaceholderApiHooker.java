package io.musician101.morefish.spigot.hooker;

import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.spigot.SpigotMoreFish;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SpigotPlaceholderApiHooker implements SpigotPluginHooker {

    private boolean hasHooked = false;

    @Nonnull
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook() {
        new MoreFishPlaceholder().register();
        setHasHooked(true);
    }

    public void setHasHooked(boolean hasHooked) {
        this.hasHooked = hasHooked;
    }

    @Nonnull
    public String tryReplacing(@Nonnull String string, @Nullable UUID player) {
        return PlaceholderAPI.setPlaceholders(player == null ? null : Bukkit.getPlayer(player), string);
    }

    public static final class MoreFishPlaceholder extends PlaceholderExpansion {

        private final FishingCompetition competition = SpigotMoreFish.getInstance().getCompetition();

        @Nonnull
        @Override
        public String getAuthor() {
            return "Musician101";
        }

        @Nonnull
        @Override
        public String getIdentifier() {
            return "morefish";
        }

        @Nonnull
        @Override
        public String getVersion() {
            return SpigotMoreFish.getInstance().getDescription().getVersion();
        }

        @Nullable
        public String onPlaceholderRequest(@Nullable Player player, @Nonnull String identifier) {
            if (identifier.startsWith("top_player_")) {
                int number = Integer.parseInt(identifier.replace("top_player_", ""));
                if (competition.getRanking().size() >= number) {
                    return Bukkit.getOfflinePlayer(competition.recordOf(number).getFisher()).getName();
                }

                return "no one";
            }
            else if (identifier.startsWith("top_fish_length_")) {
                int number = Integer.parseInt(identifier.replace("top_fish_length_", ""));
                if (competition.getRanking().size() >= number) {
                    return String.valueOf(competition.recordOf(number).getFish().getLength());
                }

                return "0.0";
            }
            else if (identifier.startsWith("top_fish_")) {
                int number = Integer.parseInt(identifier.replace("top_fish_", ""));
                if (competition.getRanking().size() >= number) {
                    return competition.recordOf(number).getFish().getType().getName();
                }

                return "none";
            }
            else if (player != null) {
                if (identifier.equals("rank")) {
                    if (competition.containsContestant(player.getUniqueId())) {
                        Record record = competition.recordOf(player.getUniqueId());
                        return String.valueOf(competition.rankNumberOf(record));
                    }

                    return "0";
                }
                else if (identifier.equals("fish")) {
                    if (competition.containsContestant(player.getUniqueId())) {
                        return competition.recordOf(player.getUniqueId()).getFish().getType().getName();
                    }

                    return "none";
                }
            }

            return null;
        }
    }
}
