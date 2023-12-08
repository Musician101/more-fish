package me.elsiff.morefish.common.fishing;

import java.util.List;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public record FishRarity<C extends FishingCompetition<F>, F extends Fish<?>, I, P>(@NotNull String name,
                                                                                   @NotNull String displayName,
                                                                                   boolean isDefault,
                                                                                   double probability,
                                                                                   @NotNull TextColor color,
                                                                                   @NotNull List<CatchHandler<F, P>> catchHandlers,
                                                                                   @NotNull List<FishCondition<C, I, P>> conditions,
                                                                                   @NotNull PlayerAnnouncement<P> catchAnnouncement,
                                                                                   boolean hasNotFishItemFormat,
                                                                                   boolean noDisplay,
                                                                                   boolean hasCatchFirework,
                                                                                   double additionalPrice) implements Comparable<FishRarity<C, F, I, P>> {

    @Override
    public int compareTo(@NotNull FishRarity o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
    }

    @Override
    public String toString() {
        return "FishRarity[" + "name=" + name + ", " + "displayName=" + displayName + ", " + "isDefault=" + isDefault + ", " + "probability=" + probability + ", " + "color=" + color + ", " + "catchHandlers=" + catchHandlers + ", " + "conditions=" + conditions + ", " + "catchAnnouncement=" + catchAnnouncement + ", " + "hasNotFishItemFormat=" + hasNotFishItemFormat + ", " + "noDisplay=" + noDisplay + ", " + "hasCatchFirework=" + hasCatchFirework + ", " + "additionalPrice=" + additionalPrice + ']';
    }
}
