package me.elsiff.morefish.fishing;

import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.condition.FishCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record FishRarity(@NotNull String name, @NotNull String displayName, boolean isDefault, double probability,
                         @NotNull String color, @NotNull List<CatchHandler> catchHandlers,
                         @NotNull List<FishCondition> conditions, @NotNull PlayerAnnouncement catchAnnouncement,
                         @NotNull Map<Integer, Double> luckOfTheSeaChances,
                         boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework,
                         double additionalPrice, int customModelData, boolean filterDefaultEnabled) implements Comparable<FishRarity> {

    @Override
    public int compareTo(@NotNull FishRarity o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
    }
}
