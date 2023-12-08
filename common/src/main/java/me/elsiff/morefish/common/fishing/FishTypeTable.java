package me.elsiff.morefish.common.fishing;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.common.configuration.Lang;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public abstract class FishTypeTable<C extends FishingCompetition<F>, F extends Fish<T>, I, P, S, T extends FishType<C, F, I, P, S>> {

    private static final Gson GSON = new Gson();
    protected final BiMap<FishRarity<C, F, I, P>, List<T>> map = HashBiMap.create();
    private JsonObject fish = new JsonObject();

    protected abstract List<CatchHandler<F, P>> getCatchHandlers(@NotNull JsonObject jsonObject);

    @NotNull
    public Optional<FishRarity<C, F, I, P>> getDefaultRarity() {
        Set<FishRarity<C, F, I, P>> defaultRarities = getRarities().stream().filter(FishRarity::isDefault).collect(Collectors.toSet());
        if (defaultRarities.size() <= 1) {
            if (!defaultRarities.isEmpty()) {
                return Optional.of(defaultRarities.iterator().next());
            }
        }

        return Optional.empty();
    }

    protected abstract List<FishCondition<C, I, P>> getFishConditions(@NotNull JsonObject jsonObject);

    protected abstract Path getFishDir();

    @NotNull
    protected abstract T getFishType(String name, FishRarity<C, F, I, P> fishRarity, String displayName, double minLength, double maxLength, S itemStack, List<CatchHandler<F, P>> catchHandlers, PlayerAnnouncement<P> announcement, List<FishCondition<C, I, P>> conditions, boolean skipItemFormat, boolean noDisplay, boolean firework, double additionalPrice);

    @NotNull
    public Optional<JsonObject> getItemFormat() {
        return Optional.ofNullable(fish.getAsJsonObject("item-format"));
    }

    private boolean getOrDefault(JsonObject json, String key, boolean def) {
        return json.has(key) ? json.get(key).getAsBoolean() : def;
    }

    protected boolean getOrDefaultFalse(JsonObject json, String key) {
        return json.has(key) && json.get(key).getAsBoolean();
    }

    private double getOrDefaultZero(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsDouble() : 0D;
    }

    protected abstract PlayerAnnouncement<P> getPlayerAnnouncement(JsonObject jsonObject);

    @NotNull
    public Set<FishRarity<C, F, I, P>> getRarities() {
        return map.keySet();
    }

    protected List<String> getStringList(JsonArray json) {
        return json.asList().stream().map(JsonElement::getAsString).toList();
    }

    @NotNull
    public List<T> getTypes() {
        return map.values().stream().flatMap(List::stream).toList();
    }

    public void load() {
        map.clear();
        Path fishDir = getFishDir();
        String fishFile = "fish.json";
        try {
            saveDefaultFile(fishFile);
            fish = GSON.fromJson(Files.readString(fishDir.resolve(fishFile)), JsonObject.class);
            JsonObject raritiesConfig = fish.getAsJsonObject("rarity-list");
            if (raritiesConfig != null) {
                List<FishRarity<C, F, I, P>> rarities = raritiesConfig.keySet().stream().map(key -> {
                    if (!raritiesConfig.has(key)) {
                        return null;
                    }

                    JsonObject json = raritiesConfig.getAsJsonObject(key);
                    List<CatchHandler<F, P>> catchHandlers = getCatchHandlers(json);
                    String displayName = json.get("display-name").getAsString();
                    if (displayName == null) {
                        throw new IllegalArgumentException("display-name is missing from rarity-list." + key + ".");
                    }

                    boolean isDefault = getOrDefaultFalse(json, "default");
                    double chance = getOrDefaultZero(json, "chance") / 100D;
                    TextColor color = Lang.getColor(json.get("color").getAsString());
                    PlayerAnnouncement<P> announcement = getPlayerAnnouncement(json);
                    boolean skipItemFormat = getOrDefaultFalse(json, "skip-item-format");
                    boolean noDisplay = getOrDefaultFalse(json, "no-display");
                    boolean firework = getOrDefaultFalse(json, "firework");
                    double additionalPrice = getOrDefaultZero(json, "additional-price");
                    List<FishCondition<C, I, P>> conditions = getFishConditions(json);
                    return new FishRarity<>(key, displayName, isDefault, chance, color, catchHandlers, conditions, announcement, skipItemFormat, noDisplay, firework, additionalPrice);
                }).filter(Objects::nonNull).toList();
                rarities.forEach(fishRarity -> {
                    String fishRarityFile = fishRarity.name() + ".json";
                    saveDefaultFile(fishRarityFile);

                    try {
                        JsonObject fishList = GSON.fromJson(Files.readString(fishDir.resolve(fishRarityFile)), JsonObject.class);
                        fishList.keySet().forEach(rarityName -> {
                            if (!fishList.has(rarityName)) {
                                return;
                            }

                            List<T> fishTypes = fishList.keySet().stream().map(name -> {
                                JsonObject json = fishList.getAsJsonObject(name);
                                List<CatchHandler<F, P>> catchHandlers = getCatchHandlers(json);
                                String displayName = json.get("display-name").getAsString();
                                if (displayName == null) {
                                    throw new IllegalArgumentException("display-name is missing from fish-list." + rarityName + "." + name + ".");
                                }

                                double minLength = json.get("length-min").getAsDouble();
                                double maxLength = json.get("length-max").getAsDouble();
                                S itemStack = loadItemStack(name, json.getAsJsonObject("icon"), "fish-list." + rarityName + "." + name);
                                PlayerAnnouncement<P> announcement = getPlayerAnnouncement(json);
                                List<FishCondition<C, I, P>> conditions = Stream.concat(getFishConditions(json).stream(), fishRarity.conditions().stream()).toList();
                                boolean skipItemFormat = getOrDefault(json, "skip-item-format", fishRarity.hasNotFishItemFormat());
                                boolean noDisplay = getOrDefault(json, "no-display", fishRarity.noDisplay());
                                boolean firework = getOrDefault(json, "firework", fishRarity.hasCatchFirework());
                                double additionalPrice = fishRarity.additionalPrice() + getOrDefaultZero(json, "additional-price");
                                return getFishType(name, fishRarity, displayName, minLength, maxLength, itemStack, catchHandlers, announcement, conditions, skipItemFormat, noDisplay, firework, additionalPrice);
                            }).toList();
                            map.put(fishRarity, fishTypes);
                        });
                    }
                    catch (IOException e) {
                        logError("Failed to load " + fishRarityFile, e);
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        catch (IOException e) {
            logError("Failed to load " + fishFile, e);
        }
    }

    @NotNull
    protected abstract S loadItemStack(String name, JsonObject json, String path);

    protected abstract void logError(@NotNull String message, @NotNull Throwable throwable);

    @NotNull
    public FishRarity<C, F, I, P> pickRandomRarity() {
        double probabilitySum = getRarities().stream().filter(rarity -> !rarity.isDefault()).mapToDouble(FishRarity::probability).sum();
        if (probabilitySum >= 1.0) {
            throw new IllegalStateException("Sum of rarity probabilities must not be bigger than 1.0");
        }

        Set<FishRarity<C, F, I, P>> rarities = getRarities();
        double randomVal = new Random().nextDouble();
        double chanceSum = 0.0;
        for (FishRarity<C, F, I, P> rarity : rarities) {
            if (!rarity.isDefault()) {
                chanceSum += rarity.probability();
                if (randomVal <= chanceSum) {
                    return rarity;
                }
            }
        }

        return getDefaultRarity().orElseThrow(() -> new IllegalStateException("Default rarity doesn't exist"));
    }

    @NotNull
    public FishType<C, F, I, P, S> pickRandomType(@NotNull I caught, @NotNull P fisher, @NotNull C competition) {
        return pickRandomType(caught, fisher, competition, pickRandomRarity());
    }

    @NotNull
    public FishType<C, F, I, P, S> pickRandomType(@NotNull I caught, @NotNull P fisher, @NotNull C competition, @NotNull FishRarity<C, F, I, P> rarity) {
        if (!map.containsKey(rarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        List<T> types = map.get(rarity).stream().filter(type -> type.conditions().stream().allMatch(condition -> condition.check(caught, fisher, competition))).toList();
        if (types.isEmpty()) {
            return pickRandomType(caught, fisher, competition);
        }

        return types.get(new Random().nextInt(types.size()));
    }

    protected abstract void saveDefaultFile(String fileName);
}
