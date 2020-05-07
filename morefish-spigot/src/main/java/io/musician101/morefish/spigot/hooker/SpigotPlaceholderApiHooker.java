package io.musician101.morefish.spigot.hooker;

import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }

    @Nonnull
    public final String tryReplacing(@Nonnull String string, @Nullable Player player) {
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    public static final class MoreFishPlaceholder extends PlaceholderExpansion {

        private final FishingCompetition<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> competition = SpigotMoreFish.getInstance().getCompetition();

        @Override
        public String getAuthor() {
            return "Musician101";
        }

        @Override
        public String getIdentifier() {
            return "morefish";
        }

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
                        Record<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> record = competition.recordOf(player.getUniqueId());
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
