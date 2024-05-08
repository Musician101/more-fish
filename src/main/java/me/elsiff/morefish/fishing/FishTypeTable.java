package me.elsiff.morefish.fishing;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.fishing.catchhandler.CatchCommandExecutor;
import me.elsiff.morefish.fishing.catchhandler.CatchFireworkSpawner;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.condition.FishCondition;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.replace;

public final class FishTypeTable {

    private static final Gson GSON = new Gson();
    private final BiMap<FishRarity, List<FishType>> map = HashBiMap.create();
    private final Random random = new Random();
    private JsonObject fish = new JsonObject();

    @NotNull
    public Optional<FishRarity> getDefaultRarity() {
        Set<FishRarity> defaultRarities = getRarities().stream().filter(FishRarity::isDefault).collect(Collectors.toSet());
        if (defaultRarities.size() <= 1) {
            if (!defaultRarities.isEmpty()) {
                return Optional.of(defaultRarities.iterator().next());
            }
        }

        return Optional.empty();
    }

    @NotNull
    public Optional<JsonObject> getItemFormat() {
        return Optional.ofNullable(fish.getAsJsonObject("item-format"));
    }

    private boolean getOrDefault(JsonObject json, String key, boolean def) {
        return json.has(key) ? json.get(key).getAsBoolean() : def;
    }

    private boolean getOrDefaultFalse(JsonObject json, String key) {
        return json.has(key) && json.get(key).getAsBoolean();
    }

    private double getOrDefaultZero(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsDouble() : 0D;
    }

    @NotNull
    public Set<FishRarity> getRarities() {
        return map.keySet();
    }

    private List<String> getStringList(JsonArray json) {
        return json.asList().stream().map(JsonElement::getAsString).toList();
    }

    @NotNull
    public List<FishType> getTypes() {
        return map.values().stream().flatMap(List::stream).toList();
    }

    public void load() {
        map.clear();
        Path fishDir = getPlugin().getDataFolder().toPath().resolve("fish");
        String fishFile = "fish.json";
        try {
            getPlugin().saveResource("fish/" + fishFile, false);
            fish = GSON.fromJson(Files.readString(fishDir.resolve(fishFile)), JsonObject.class);
            JsonObject raritiesConfig = fish.getAsJsonObject("rarity-list");
            if (raritiesConfig != null) {
                List<FishRarity> rarities = raritiesConfig.keySet().stream().map(key -> {
                    if (!raritiesConfig.has(key)) {
                        return null;
                    }

                    JsonObject json = raritiesConfig.getAsJsonObject(key);
                    List<CatchHandler> catchHandlers = new ArrayList<>();
                    if (json.has("commands")) {
                        catchHandlers.add(new CatchCommandExecutor(getStringList(json.getAsJsonArray("commands"))));
                    }
                    else if (getOrDefaultFalse(json, "firework")) {
                        catchHandlers.add(new CatchFireworkSpawner());
                    }

                    String displayName = json.get("display-name").getAsString();
                    if (displayName == null) {
                        throw new IllegalArgumentException("display-name is missing from rarity-list." + key + ".");
                    }

                    JsonObject losJson = json.getAsJsonObject("luck-of-the-sea");
                    Map<Integer, Double> luckOfTheSeaChances = new HashMap<>();
                    if (json.has("luck-of-the-sea")) {
                        luckOfTheSeaChances = losJson.entrySet().stream().collect(Collectors.toMap(e -> Integer.parseInt(e.getKey()), e -> e.getValue().getAsDouble()));
                    }

                    boolean isDefault = getOrDefaultFalse(json, "default");
                    double chance = getOrDefaultZero(json, "chance") / 100D;
                    String color = json.get("color").getAsString();
                    PlayerAnnouncement announcement = PlayerAnnouncement.fromConfigOrDefault(json, "catch-announce", PlayerAnnouncement.ofServerBroadcast());
                    boolean skipItemFormat = getOrDefaultFalse(json, "skip-item-format");
                    boolean noDisplay = getOrDefaultFalse(json, "no-display");
                    boolean firework = getOrDefaultFalse(json, "firework");
                    double additionalPrice = getOrDefaultZero(json, "additional-price");
                    List<FishCondition> conditions = FishCondition.loadFrom(json, "conditions");
                    boolean glow = getOrDefaultFalse(json, "glow");
                    int customModelData = 0;
                    if (json.has("custom-model-data")) {
                        customModelData = json.get("custom-model-data").getAsInt();
                    }

                    return new FishRarity(key, displayName, isDefault, chance, color, catchHandlers, conditions, announcement, luckOfTheSeaChances, skipItemFormat, noDisplay, firework, additionalPrice, customModelData, glow);
                }).filter(Objects::nonNull).toList();
                rarities.forEach(fishRarity -> {
                    String fishRarityFile = fishRarity.name() + ".json";
                    try {
                        getPlugin().saveResource("fish/" + fishRarityFile, false);
                    }
                    catch (IllegalArgumentException e) {
                        getPlugin().getLogger().warning("Could not find fish/" + fishRarityFile + " in plugin jar. This message can be ignored if the rarity is not in fish.json");
                    }

                    try {
                        JsonObject fishList = GSON.fromJson(Files.readString(fishDir.resolve(fishRarityFile)), JsonObject.class);
                        List<FishType> fishTypes = fishList.keySet().stream().map(name -> {
                            JsonObject json = fishList.getAsJsonObject(name);
                            List<CatchHandler> catchHandlers = new ArrayList<>(fishRarity.catchHandlers());
                            if (json.has("commands")) {
                                catchHandlers.add(new CatchCommandExecutor(getStringList(json.getAsJsonArray("commands"))));
                            }
                            else if (getOrDefaultFalse(json, "firework")) {
                                catchHandlers.add(new CatchFireworkSpawner());
                            }

                            String displayName = json.get("display-name").getAsString();
                            if (displayName == null) {
                                throw new IllegalArgumentException("display-name is missing from fish-list." + fishRarity.name() + "." + name + ".");
                            }

                            Map<Integer, Double> luckOfTheSeaChances = new HashMap<>(fishRarity.luckOfTheSeaChances());
                            if (json.has("luck-of-the-sea")) {
                                luckOfTheSeaChances = json.getAsJsonObject("luck-of-the-sea").entrySet().stream().collect(Collectors.toMap(e -> Integer.parseInt(e.getKey()), e -> e.getValue().getAsDouble()));
                            }

                            double minLength = json.get("length-min").getAsDouble();
                            double maxLength = json.get("length-max").getAsDouble();
                            ItemStack itemStack = loadItemStack(name, json.getAsJsonObject("icon"), fishRarity.customModelData(), "fish-list." + fishRarity.name() + "." + name);
                            PlayerAnnouncement announcement = PlayerAnnouncement.fromConfigOrDefault(json, "catch-announce", fishRarity.catchAnnouncement());
                            List<FishCondition> conditions = Stream.concat(FishCondition.loadFrom(json, "conditions").stream(), fishRarity.conditions().stream()).toList();
                            boolean skipItemFormat = getOrDefault(json, "skip-item-format", fishRarity.hasNotFishItemFormat());
                            boolean noDisplay = getOrDefault(json, "no-display", fishRarity.noDisplay());
                            boolean firework = getOrDefault(json, "firework", fishRarity.hasCatchFirework());
                            double additionalPrice = fishRarity.additionalPrice() + getOrDefaultZero(json, "additional-price");
                            return new FishType(name, fishRarity, displayName, minLength, maxLength, itemStack, catchHandlers, announcement, conditions, luckOfTheSeaChances, skipItemFormat, noDisplay, firework, additionalPrice);
                        }).toList();
                        map.put(fishRarity, fishTypes);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        catch (IOException e) {
            getPlugin().getLogger().severe("Failed to load " + fishFile);
        }
    }

    private ItemStack loadItemStack(String name, JsonObject json, int customModelData, String path) {
        if (json == null) {
            throw new IllegalArgumentException("icon is missing from " + name);
        }

        String id = json.get("id").getAsString();
        if (id == null) {
            throw new IllegalArgumentException("id is missing from " + path + ".icon");
        }

        Material material = Material.matchMaterial(id);
        if (material == null) {
            throw new IllegalArgumentException("id " + id + " in " + path + ".icon is not a valid item ID.");
        }

        int amount = json.has("amount") ? json.get("amount").getAsInt() : 1;
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (json.has("lore")) {
            List<Component> lore = json.getAsJsonArray("lore").asList().stream().map(content -> replace(content.getAsString())).toList();
            itemMeta.lore(lore);
        }

        if (json.has("enchantments")) {
            json.getAsJsonArray("enchantments").asList().stream().map(JsonElement::getAsString).map(string -> string.split("\\|")).forEach(tokens -> {
                NamespacedKey key = tokens[0].contains(":") ? NamespacedKey.fromString(tokens[0]) : NamespacedKey.minecraft(tokens[0]);
                Enchantment enchantment;
                if (key != null && (enchantment = Registry.ENCHANTMENT.get(key)) != null) {
                    int level = Integer.parseInt(tokens[1]);
                    if (itemMeta instanceof EnchantmentStorageMeta esm) {
                        esm.addStoredEnchant(enchantment, level, true);
                    }
                    else {
                        itemMeta.addEnchant(enchantment, level, true);
                    }
                }
            });
        }

        itemMeta.setUnbreakable(json.has("unbreakable") && json.get("unbreakable").getAsBoolean());
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(json.has("durability") ? json.get("durability").getAsInt() : 0);
        }

        if (itemMeta instanceof SkullMeta) {
            if (json.has("skull-uuid")) {
                ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(json.get("skull-uuid").getAsString())));
            }

            if (json.has("skull-texture")) {
                PlayerProfile profile = Bukkit.createProfile(name + "_skull_texture");
                profile.setProperty(new ProfileProperty("textures", json.get("skull-texture").getAsString()));
                ((SkullMeta) itemMeta).setPlayerProfile(profile);
            }
        }

        if (json.has("custom-model-data")) {
            customModelData = json.get("custom-model-data").getAsInt();
        }

        itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @NotNull
    public FishRarity pickRandomRarity() {
        double probabilitySum = getRarities().stream().filter(rarity -> !rarity.isDefault()).mapToDouble(FishRarity::probability).sum();
        if (probabilitySum >= 1.0) {
            throw new IllegalStateException("Sum of rarity probabilities must not be bigger than 1.0");
        }

        Set<FishRarity> rarities = getRarities();
        double randomVal = random.nextDouble();
        double chanceSum = 0.0;
        for (FishRarity rarity : rarities) {
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
    public List<FishType> pickRandomTypes(@NotNull Item caught, @NotNull Player fisher) {
        List<FishType> fish = new ArrayList<>();
        fish.add(pickRandomType(caught, fisher).orElseThrow(() -> new IllegalStateException("Well this isn't supposed to happen...")));
        ItemStack fishingRod = fisher.getInventory().getItemInMainHand();
        int level = fishingRod.getEnchantmentLevel(Enchantment.LUCK);
        IntStream.range(1, level + 1).mapToObj(i -> pickRandomType(caught, fisher, true, i)).filter(Optional::isPresent).map(Optional::get).forEach(fish::add);
        return fish;
    }

    @NotNull
    public Optional<FishType> pickRandomType(@NotNull Item caught, @NotNull Player fisher) {
        return pickRandomType(caught, fisher, false, 0);
    }

    @NotNull
    public Optional<FishType> pickRandomType(@NotNull Item caught, @NotNull Player fisher, boolean luckOfTheSea, int fishNumber) {
        FishRarity rarity = pickRandomRarity();
        if (!map.containsKey(rarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        double roll = random.nextDouble();
        List<FishType> types = map.get(rarity).stream().filter(type -> {
            List<FishCondition> conditions = type.conditions();
            if (!conditions.isEmpty() && conditions.stream().noneMatch(condition -> condition.check(caught, fisher))) {
                return false;
            }

            if (luckOfTheSea) {
                Map<Integer, Double> losMap = type.luckOfTheSeaChances();
                if (losMap.isEmpty()) {
                    return false;
                }

                Double chance = losMap.get(fishNumber);
                if (chance == null) {
                    return false;
                }

                return roll <= chance;
            }

            return true;
        }).toList();
        if (types.isEmpty()) {
            if (luckOfTheSea) {
                return Optional.empty();
            }

            return pickRandomType(caught, fisher);
        }

        return Optional.ofNullable(types.get(random.nextInt(types.size())));
    }
}
