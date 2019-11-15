package me.elsiff.morefish.hooker;

import javax.annotation.Nonnull;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.format.Format;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.Record;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderApiHooker implements PluginHooker {

    private boolean hasHooked = false;

    @Nonnull
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook(@Nonnull MoreFish plugin) {
        new MoreFishPlaceholder(plugin).hook();
        Format.Companion.init(this);
        setHasHooked(true);
    }

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }

    @Nonnull
    public final String tryReplacing(@Nonnull String string, @Nullable Player player) {
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    public static final class MoreFishPlaceholder extends EZPlaceholderHook {

        private final FishingCompetition competition;

        public MoreFishPlaceholder(@Nonnull MoreFish moreFish) {
            super(moreFish, "morefish");
            this.competition = moreFish.getCompetition();
        }

        @Nullable
        public String onPlaceholderRequest(@Nullable Player player, @Nonnull String identifier) {
            if (identifier.startsWith("top_player_")) {
                int number = Integer.parseInt(identifier.replace("top_player_", ""));
                if (competition.getRanking().size() >= number) {
                    return competition.recordOf(number).getFisher().getName();
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
                    if (competition.containsContestant(player)) {
                        Record record = competition.recordOf(player);
                        return String.valueOf(competition.rankNumberOf(record));
                    }

                    return "0";
                }
                else if (identifier.equals("fish")) {
                    if (competition.containsContestant(player)) {
                        return competition.recordOf(player).getFish().getType().getName();
                    }

                    return "none";
                }
            }

            return null;
        }
    }
}
