package me.elsiff.morefish.fishing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class MutableFishTypeTable extends HashMap<FishRarity, Set<FishType>> implements FishTypeTable {

    @Nonnull
    public Optional<FishRarity> getDefaultRarity() {
        Set<FishRarity> defaultRarities = getRarities().stream().filter(FishRarity::getDefault).collect(Collectors.toSet());
        if (defaultRarities.size() <= 1) {
            if (!defaultRarities.isEmpty()) {
                return Optional.of(defaultRarities.iterator().next());
            }
        }

        return Optional.empty();
    }

    @Nonnull
    public Set<FishRarity> getRarities() {
        return keySet();
    }

    @Nonnull
    public Set<FishType> getTypes() {
        return values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Nonnull
    public FishRarity pickRandomRarity() {
        double probabilitySum = getRarities().stream().filter(rarity -> !rarity.getDefault()).mapToDouble(FishRarity::getProbability).sum();
        if (probabilitySum >= 1.0) {
            throw new IllegalStateException("Sum of rarity probabilities must not be bigger than 1.0");
        }

        Set<FishRarity> rarities = getRarities();
        double randomVal = new Random().nextDouble();
        double chanceSum = 0.0;
        for (FishRarity rarity : rarities) {
            if (!rarity.getDefault()) {
                chanceSum += rarity.getProbability();
                if (randomVal <= chanceSum) {
                    return rarity;
                }
            }
        }

        return getDefaultRarity().orElseThrow(() -> new IllegalStateException("Default rarity doesn't exist"));
    }

    @Nonnull
    public FishType pickRandomType(@Nonnull FishRarity fishRarity) {
        if (!containsKey(fishRarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        return new ArrayList<>(get(fishRarity)).get(new Random().nextInt(size()));
    }

    @Nonnull
    public FishType pickRandomType(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition competition, @Nonnull FishRarity rarity) {
        if (!containsKey(rarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        List<FishType> types = get(rarity).stream().filter(type -> type.getConditions().stream().allMatch(condition -> condition.check(caught, fisher, competition))).collect(Collectors.toList());
        if (types.isEmpty()) {
            throw new IllegalStateException("No fish type matches given condition");
        }

        return types.get(new Random().nextInt(types.size()));
    }
}
