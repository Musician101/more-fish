package me.elsiff.morefish.fishing;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.catchhandler.CatchCommandExecutor;
import me.elsiff.morefish.fishing.catchhandler.CatchFireworkSpawner;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.condition.FishCondition;
import me.elsiff.morefish.hooker.PluginHooker;
import me.elsiff.morefish.hooker.ProtocolLibHooker;
import me.elsiff.morefish.hooker.SkullNbtHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishTypeTable {

    private static final Gson GSON = new Gson();
    private final BiMap<FishRarity, List<FishType>> map = HashBiMap.create();
    private JsonObject fish = new JsonObject();

    @Nonnull
    public Optional<FishRarity> getDefaultRarity() {
        Set<FishRarity> defaultRarities = getRarities().stream().filter(FishRarity::isDefault).collect(Collectors.toSet());
        if (defaultRarities.size() <= 1) {
            if (!defaultRarities.isEmpty()) {
                return Optional.of(defaultRarities.iterator().next());
            }
        }

        return Optional.empty();
    }

    @Nonnull
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

    @Nonnull
    public Set<FishRarity> getRarities() {
        return map.keySet();
    }

    private List<String> getStringList(JsonArray json) {
        return json.asList().stream().map(JsonElement::getAsString).toList();
    }

    @Nonnull
    public List<FishType> getTypes() {
        return map.values().stream().flatMap(List::stream).toList();
    }

    public void load() {
        map.clear();
        File fishDir = getPlugin().getDataFolder().toPath().resolve("fish").toFile();
        String fishFile = "fish.json";
        try {
            getPlugin().saveResource("fish/" + fishFile, false);
            fish = GSON.fromJson(Files.readString(new File(fishDir, fishFile).toPath()), JsonObject.class);
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

                    boolean isDefault = getOrDefaultFalse(json, "default");
                    double chance = getOrDefaultZero(json, "chance") / 100D;
                    TextColor color = Lang.getColor(json.get("color").getAsString());
                    PlayerAnnouncement announcement = PlayerAnnouncement.fromConfigOrDefault(json, "catch-announce", Config.getDefaultCatchAnnouncement());
                    boolean skipItemFormat = getOrDefaultFalse(json, "skip-item-format");
                    boolean noDisplay = getOrDefaultFalse(json, "no-display");
                    boolean firework = getOrDefaultFalse(json, "firework");
                    double additionalPrice = getOrDefaultZero(json, "additional-price");
                    List<FishCondition> conditions = FishCondition.loadFrom(json, "conditions");
                    return new FishRarity(key, displayName, isDefault, chance, color, catchHandlers, conditions, announcement, skipItemFormat, noDisplay, firework, additionalPrice);
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
                        JsonObject fishList = GSON.fromJson(Files.readString(new File(fishDir, fishRarityFile).toPath()), JsonObject.class);
                        fishList.keySet().forEach(rarityName -> {
                            if (!fishList.has(rarityName)) {
                                return;
                            }

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
                                    throw new IllegalArgumentException("display-name is missing from fish-list." + rarityName + "." + name + ".");
                                }

                                double minLength = json.get("length-min").getAsDouble();
                                double maxLength = json.get("length-max").getAsDouble();
                                ItemStack itemStack = loadItemStack(name, json.getAsJsonObject("icon"), "fish-list." + rarityName + "." + name);
                                PlayerAnnouncement announcement = PlayerAnnouncement.fromConfigOrDefault(json, "catch-announce", fishRarity.catchAnnouncement());
                                List<FishCondition> conditions = Stream.concat(FishCondition.loadFrom(json, "conditions").stream(), fishRarity.conditions().stream()).toList();
                                boolean skipItemFormat = getOrDefault(json, "skip-item-format", fishRarity.hasNotFishItemFormat());
                                boolean noDisplay = getOrDefault(json, "no-display", fishRarity.noDisplay());
                                boolean firework = getOrDefault(json, "firework", fishRarity.hasCatchFirework());
                                double additionalPrice = fishRarity.additionalPrice() + getOrDefaultZero(json, "additional-price");
                                return new FishType(name, fishRarity, displayName, minLength, maxLength, itemStack, catchHandlers, announcement, conditions, skipItemFormat, noDisplay, firework, additionalPrice);
                            }).toList();
                            map.put(fishRarity, fishTypes);
                        });
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

    private ItemStack loadItemStack(String name, JsonObject json, String path) {
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
            List<Component> lore = StreamSupport.stream(json.getAsJsonArray("lore").spliterator(), false).map(content -> GsonComponentSerializer.gson().deserialize(content.toString())).toList();
            itemMeta.lore(lore);
        }
        if (json.has("enchantments")) {
            StreamSupport.stream(json.getAsJsonArray("enchantments").spliterator(), false).map(JsonElement::getAsString).map(string -> string.split("\\|")).forEach(tokens -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(tokens[0]));
                if (enchantment != null) {
                    int level = Integer.parseInt(tokens[1]);
                    itemMeta.addEnchant(enchantment, level, true);
                }
            });
        }

        itemMeta.setUnbreakable(json.has("unbreakable") && json.get("unbreakable").getAsBoolean());
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(json.has("durability") ? json.get("durability").getAsInt() : 0);
        }

        if (json.has("skull-uuid") && itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("skull-uuid")));
        }

        itemStack.setItemMeta(itemMeta);
        if (json.has("skull-texture")) {
            ProtocolLibHooker protocolLib = new ProtocolLibHooker();
            PluginHooker.checkHooked(protocolLib);
            SkullNbtHandler skullNbtHandler = protocolLib.skullNbtHandler;
            if (skullNbtHandler != null) {
                String skullTexture = json.get("skull-texture").getAsString();
                if (skullTexture != null) {
                    itemStack = skullNbtHandler.writeTexture(itemStack, skullTexture);
                }
            }
        }

        return itemStack;
    }

    @Nonnull
    public FishRarity pickRandomRarity() {
        double probabilitySum = getRarities().stream().filter(rarity -> !rarity.isDefault()).mapToDouble(FishRarity::probability).sum();
        if (probabilitySum >= 1.0) {
            throw new IllegalStateException("Sum of rarity probabilities must not be bigger than 1.0");
        }

        Set<FishRarity> rarities = getRarities();
        double randomVal = new Random().nextDouble();
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

    @Nonnull
    public FishType pickRandomType(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition competition) {
        return pickRandomType(caught, fisher, competition, pickRandomRarity());
    }

    @Nonnull
    public FishType pickRandomType(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition competition, @Nonnull FishRarity rarity) {
        if (!map.containsKey(rarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        List<FishType> types = map.get(rarity).stream().filter(type -> type.conditions().stream().allMatch(condition -> condition.check(caught, fisher, competition))).toList();
        if (types.isEmpty()) {
            return pickRandomType(caught, fisher, competition);
        }

        return types.get(new Random().nextInt(types.size()));
    }
}
