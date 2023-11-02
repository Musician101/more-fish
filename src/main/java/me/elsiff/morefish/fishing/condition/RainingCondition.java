package me.elsiff.morefish.fishing.condition;

import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record RainingCondition(boolean raining) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull FishingCompetition fishingCompetition) {
        return caught.getWorld().hasStorm() == this.raining;
    }
}
