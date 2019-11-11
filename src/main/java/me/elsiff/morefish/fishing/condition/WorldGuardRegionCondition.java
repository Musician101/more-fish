package me.elsiff.morefish.fishing.condition;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.hooker.WorldGuardHooker;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class WorldGuardRegionCondition implements FishCondition {

    private final String regionId;
    private final WorldGuardHooker worldGuardHooker;

    public WorldGuardRegionCondition(@Nonnull WorldGuardHooker worldGuardHooker, @Nonnull String regionId) {
        super();
        this.worldGuardHooker = worldGuardHooker;
        this.regionId = regionId;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        return worldGuardHooker.containsLocation(regionId, caught.getLocation());
    }
}
