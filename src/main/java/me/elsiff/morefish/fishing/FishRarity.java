package me.elsiff.morefish.fishing;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.condition.FishCondition;
import net.kyori.adventure.text.format.TextColor;

public record FishRarity(@Nonnull String name, @Nonnull String displayName, boolean isDefault, double probability,
                         @Nonnull TextColor color, @Nonnull List<CatchHandler> catchHandlers,
                         @Nonnull List<FishCondition> conditions, @Nonnull PlayerAnnouncement catchAnnouncement,
                         boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework,
                         double additionalPrice) implements Comparable<FishRarity> {

    @Override
    public int compareTo(@Nonnull FishRarity o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
    }
}
