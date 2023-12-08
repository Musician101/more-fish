package me.elsiff.morefish.fishing;

import java.util.List;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.condition.FishCondition;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public record FishRarity(@NotNull String name, @NotNull String displayName, boolean isDefault, double probability,
                         @NotNull TextColor color, @NotNull List<CatchHandler> catchHandlers,
                         @NotNull List<FishCondition> conditions, @NotNull PlayerAnnouncement catchAnnouncement,
                         boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework,
                         double additionalPrice) implements Comparable<FishRarity> {

    @Override
    public int compareTo(@NotNull FishRarity o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
    }
}
