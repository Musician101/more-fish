package io.musician101.morefish.common.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.musician101.morefish.common.ConfigurateLoader;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import io.musician101.musicianlibrary.java.minecraft.common.config.AbstractConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;

public final class FishConfig<C extends FishCondition<I, P>, I, P, R extends FishRarity<?, ?, ?>, T extends FishType<?, ?, C, ?, ?>> extends AbstractConfig {

    private static final int CONFIG_VERSION = 300;
    protected final Multimap<R, T> map = HashMultimap.create();
    @Nonnull
    private final Function<Map<Object, ? extends ConfigurationNode>, List<R>> fishRarityLoader;
    @Nonnull
    private final BiFunction<R, Entry<Object, ? extends ConfigurationNode>, Entry<R, T>> fishTypeLoader;
    @Nonnull
    private final ConfigurateLoader loader;
    @Nonnull
    private final Supplier<File> saveDefaultFile;
    @Nonnull
    private final BiConsumer<Integer, Integer> versionChecker;
    protected String displayNameFormat;
    @Nonnull
    protected List<String> loreFormat = new ArrayList<>();

    public FishConfig(@Nonnull File configFile, @Nonnull Supplier<File> saveDefaultFile, @Nonnull ConfigurateLoader loader, @Nonnull Function<Map<Object, ? extends ConfigurationNode>, List<R>> fishRarityLoader, @Nonnull BiFunction<R, Entry<Object, ? extends ConfigurationNode>, Entry<R, T>> fishTypeLoader, @Nonnull BiConsumer<Integer, Integer> versionChecker) {
        super(configFile);
        this.saveDefaultFile = saveDefaultFile;
        this.loader = loader;
        this.fishRarityLoader = fishRarityLoader;
        this.fishTypeLoader = fishTypeLoader;
        this.versionChecker = versionChecker;
    }

    @Nonnull
    public Optional<R> getDefaultRarity() {
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
    public Set<R> getRarities() {
        return map.keySet();
    }

    @Nonnull
    public Collection<T> getTypes() {
        return map.values();
    }

    @Nonnull
    public R pickRandomRarity() {
        double probabilitySum = getRarities().stream().filter(rarity -> !rarity.isDefault()).mapToDouble(FishRarity::getProbability).sum();
        if (probabilitySum >= 1.0) {
            throw new IllegalStateException("Sum of rarity probabilities must not be bigger than 1.0");
        }

        Set<R> rarities = getRarities();
        double randomVal = new Random().nextDouble();
        double chanceSum = 0.0;
        for (R rarity : rarities) {
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
    public T pickRandomType(@Nonnull I caught, @Nonnull P fisher) {
        return pickRandomType(caught, fisher, pickRandomRarity());
    }

    @Nonnull
    public T pickRandomType(@Nonnull I caught, @Nonnull P fisher, @Nonnull R rarity) {
        if (!map.containsKey(rarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        List<T> types = map.get(rarity).stream().filter(type -> type.getConditions().stream().allMatch(condition -> condition.check(caught, fisher))).collect(Collectors.toList());
        if (types.isEmpty()) {
            throw new IllegalStateException("No fish type matches given condition");
        }

        return types.get(new Random().nextInt(types.size()));
    }

    @Override
    public void reload() {
        map.clear();
        configFile = saveDefaultFile.get();
        try {
            ConfigurationNode fish = loader.get(configFile.toPath()).load();
            ConfigurationNode itemFormat = fish.getNode("item-format");
            displayNameFormat = itemFormat.getNode("display-name").getString();
            loreFormat = itemFormat.getNode("lore").getList(Object::toString);
            List<R> fishRarities = fishRarityLoader.apply(fish.getNode("rarity-list").getChildrenMap());
            ConfigurationNode fishList = fish.getNode("fish-list");
            fishList.getChildrenMap().entrySet().stream().flatMap(groupByRarity -> {
                String rarityName = groupByRarity.getKey().toString();
                R rarity = fishRarities.stream().filter(fishRarity -> rarityName.equals(fishRarity.getName())).findFirst().orElseThrow(() -> new IllegalStateException("Rarity " + rarityName + " doesn't exist."));
                return groupByRarity.getValue().getChildrenMap().entrySet().stream().map(e -> fishTypeLoader.apply(rarity, e));
            }).forEach(e -> map.put(e.getKey(), e.getValue()));
            versionChecker.accept(fish.getNode("version").getInt(), CONFIG_VERSION);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
