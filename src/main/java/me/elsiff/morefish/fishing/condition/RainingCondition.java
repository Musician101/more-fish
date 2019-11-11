package me.elsiff.morefish.fishing.condition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class RainingCondition implements FishCondition {

    private final boolean raining;

    public RainingCondition(boolean raining) {
        this.raining = raining;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        return caught.getWorld().hasStorm() == this.raining;
    }
}
