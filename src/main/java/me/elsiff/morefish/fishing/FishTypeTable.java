package me.elsiff.morefish.fishing;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public interface FishTypeTable extends Map<FishRarity, Set<FishType>> {

    @Nonnull
    Optional<FishRarity> getDefaultRarity();

    @Nonnull
    Set<FishRarity> getRarities();

    @Nonnull
    Set<FishType> getTypes();

    @Nonnull
    FishRarity pickRandomRarity();

    @Nonnull
    default FishType pickRandomType() {
        return pickRandomType(pickRandomRarity());
    }

    @Nonnull
    FishType pickRandomType(@Nonnull FishRarity fishRarity);

    @Nonnull
    default FishType pickRandomType(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition competition) {
        return pickRandomType(caught, fisher, competition, pickRandomRarity());
    }

    @Nonnull
    FishType pickRandomType(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition competition, @Nonnull FishRarity rarity);
}
