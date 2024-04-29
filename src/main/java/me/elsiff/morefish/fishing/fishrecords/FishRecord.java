package me.elsiff.morefish.fishing.fishrecords;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishRarity;
import me.elsiff.morefish.fishing.FishType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class FishRecord implements Comparable<FishRecord> {

    @NotNull
    private final UUID fisher;
    private final double length;
    @NotNull
    private final String fishName;
    @NotNull
    private final String rarityName;
    private final double rarityProbability;
    private final long timestamp;

    public FishRecord(@NotNull UUID fisher, @NotNull Fish fish, long timestamp) {
        this.fisher = fisher;
        this.length = fish.length();
        FishType type = fish.type();
        FishRarity rarity = type.rarity();
        this.fishName = type.displayName();
        this.rarityName = rarity.name();
        this.rarityProbability = rarity.probability();
        this.timestamp = timestamp;
    }

    public FishRecord(@NotNull UUID fisher, double length, @NotNull String fishName, @NotNull String rarityName, double rarityProbability, long timestamp) {
        this.fisher = fisher;
        this.length = length;
        this.fishName = fishName;
        this.rarityName = rarityName;
        this.rarityProbability = rarityProbability;
        this.timestamp = timestamp;
    }

    @NotNull
    public String getFishName() {
        return fishName;
    }

    @NotNull
    public String getRarityName() {
        return rarityName;
    }

    public double getRarityProbability() {
        return rarityProbability;
    }

    public double getLength() {
        return length;
    }

    public int compareTo(@NotNull FishRecord other) {
        return Double.compare(length, other.length);
    }

    @NotNull
    public UUID fisher() {
        return fisher;
    }

    public long timestamp() {
        return timestamp;
    }
}
