package me.elsiff.morefish.fishing.condition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class LocationYCondition implements FishCondition {

    private final DoubleRange range;

    public LocationYCondition(@Nonnull DoubleRange range) {
        super();
        this.range = range;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        return range.containsDouble(fisher.getLocation().getY());
    }
}
