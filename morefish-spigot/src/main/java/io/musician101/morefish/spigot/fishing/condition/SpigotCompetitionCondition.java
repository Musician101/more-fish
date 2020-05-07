package io.musician101.morefish.spigot.fishing.condition;

import io.musician101.morefish.common.fishing.competition.FishingCompetition.State;
import io.musician101.morefish.spigot.SpigotMoreFish;
import javax.annotation.Nonnull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotCompetitionCondition implements SpigotFishCondition {

    private final State state;

    public SpigotCompetitionCondition(@Nonnull State state) {
        this.state = state;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        return SpigotMoreFish.getInstance().getCompetition().getState() == this.state;
    }
}
