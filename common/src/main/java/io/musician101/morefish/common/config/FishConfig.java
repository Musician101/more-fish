package io.musician101.morefish.common.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public record FishConfig(@Nonnull String displayNameFormat, @Nonnull List<String> loreFormat,
                         @Nonnull Multimap<FishRarity, FishType> fishMap) {

    private static final int CONFIG_VERSION = 300;

    public static FishConfig deserialize(@Nonnull ConfigurationNode node, @Nonnull BiConsumer<Integer, Integer> versionChecker, @Nonnull PlayerAnnouncement catchAnnouncement) throws SerializationException {
        String displayNameFormat = node.node("item-format", "display-name").getString();
        if (displayNameFormat == null) {
            throw new SerializationException("item-format.display-name cannot be null");
        }

        List<String> loreFormat = node.node("item-format", "lore").getList(String.class, new ArrayList<>());
        if (loreFormat == null) {
            throw new SerializationException("item-format.lore cannot be null");
        }

        List<FishRarity> fishRarities = new ArrayList<>();
        for (ConfigurationNode n : node.node("rarity-list").childrenList()) {
            fishRarities.add(FishRarity.deserialize(n, catchAnnouncement));
        }
        Multimap<FishRarity, FishType> fishMap = HashMultimap.create();
        for (Entry<Object, ? extends ConfigurationNode> entry : node.node("fish-list").childrenMap().entrySet()) {
            Object k = entry.getKey();
            ConfigurationNode fishType = entry.getValue();
            String rarityName = k.toString();
            FishRarity rarity = fishRarities.stream().filter(fishRarity -> rarityName.equals(fishRarity.getName())).findFirst().orElseThrow(() -> new SerializationException("Rarity " + rarityName + " doesn't exist."));
            fishMap.put(rarity, FishType.deserialize(rarity, fishType));
        }

        versionChecker.accept(node.node("version").getInt(), CONFIG_VERSION);
        return new FishConfig(displayNameFormat, loreFormat, fishMap);
    }

    @Nonnull
    public Optional<FishRarity> getDefaultRarity() {
        return getRarities().stream().filter(FishRarity::isDefault).findFirst();
    }

    @Nonnull
    public String getDisplayNameFormat() {
        return displayNameFormat;
    }

    @Nonnull
    public List<String> getLoreFormat() {
        return loreFormat;
    }

    @Nonnull
    public Set<FishRarity> getRarities() {
        return fishMap.keySet();
    }

    @Nonnull
    public Collection<FishType> getTypes() {
        return fishMap.values();
    }

    @Nonnull
    public FishRarity pickRandomRarity() {
        double probabilitySum = getRarities().stream().filter(rarity -> !rarity.isDefault()).mapToDouble(FishRarity::getProbability).sum();
        if (probabilitySum >= 1.0) {
            throw new IllegalStateException("Sum of rarity probabilities must not be bigger than 1.0");
        }

        Set<FishRarity> rarities = getRarities();
        double randomVal = new Random().nextDouble();
        double chanceSum = 0.0;
        for (FishRarity rarity : rarities) {
            if (!rarity.isDefault()) {
                chanceSum += rarity.getProbability();
                if (randomVal <= chanceSum) {
                    return rarity;
                }
            }
        }

        return getDefaultRarity().orElseThrow(() -> new IllegalStateException("Default rarity doesn't exist"));
    }

    @Nonnull
    public FishType pickRandomType(@Nonnull UUID caught, @Nonnull UUID fisher) {
        return pickRandomType(caught, fisher, pickRandomRarity());
    }

    @Nonnull
    public FishType pickRandomType(@Nonnull UUID caught, @Nonnull UUID fisher, @Nonnull FishRarity rarity) {
        if (!fishMap.containsKey(rarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        List<FishType> types = fishMap.get(rarity).stream().filter(type -> type.getConditions().stream().allMatch(condition -> condition.check(caught, fisher))).toList();
        if (types.isEmpty()) {
            throw new IllegalStateException("No fish type matches given condition");
        }

        return types.get(new Random().nextInt(types.size()));
    }

}
