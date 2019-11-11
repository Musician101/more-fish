package me.elsiff.morefish.fishing.condition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class ThunderingCondition implements FishCondition {

    private final boolean thundering;

    public ThunderingCondition(boolean thundering) {
        this.thundering = thundering;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        return caught.getWorld().isThundering() == this.thundering;
    }
}
