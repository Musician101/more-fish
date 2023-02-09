package me.elsiff.morefish.fishing;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.fishing.catchhandler.CatchCommandExecutor;
import me.elsiff.morefish.fishing.catchhandler.CatchFireworkSpawner;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.condition.FishCondition;
import me.elsiff.morefish.hooker.PluginHooker;
import me.elsiff.morefish.hooker.ProtocolLibHooker;
import me.elsiff.morefish.hooker.SkullNbtHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class FishTypeTable {

    private final YamlConfiguration fish = new YamlConfiguration();
    private final BiMap<FishRarity, List<FishType>> map = HashBiMap.create();

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
    public Optional<ConfigurationSection> getItemFormat() {
        return Optional.ofNullable(fish.getConfigurationSection("item-format"));
    }

    @Nonnull
    public Set<FishRarity> getRarities() {
        return map.keySet();
    }

    @Nonnull
    public List<FishType> getTypes() {
        return map.values().stream().flatMap(List::stream).toList();
    }

    public void load() {
        map.clear();
        MoreFish plugin = MoreFish.instance();
        String fishFile = "fish.yml";
        try {
            plugin.saveResource(fishFile, false);
            fish.load(new File(plugin.getDataFolder(), fishFile));
            ConfigurationSection raritiesConfig = fish.getConfigurationSection("rarity-list");
            if (raritiesConfig != null) {
                List<FishRarity> rarities = raritiesConfig.getKeys(false).stream().map(raritiesConfig::getConfigurationSection).filter(Objects::nonNull).map(cs -> {
                    List<CatchHandler> catchHandlers = new ArrayList<>();
                    if (cs.contains("commands")) {
                        catchHandlers.add(new CatchCommandExecutor(cs.getStringList("commands")));
                    }
                    else if (cs.getBoolean("firework", false)) {
                        catchHandlers.add(new CatchFireworkSpawner());
                    }

                    String displayName = cs.getString("display-name");
                    if (displayName == null) {
                        throw new IllegalArgumentException("display-name is missing from " + cs.getCurrentPath() + ".");
                    }

                    boolean isDefault = cs.getBoolean("default", false);
                    double chance = cs.getDouble("chance", 0D) / 100D;
                    ChatColor color = Optional.ofNullable(cs.getString("color")).map(String::toUpperCase).map(ChatColor::valueOf).orElse(ChatColor.RESET);
                    PlayerAnnouncement announcement = PlayerAnnouncement.fromConfigOrDefault(cs, "catch-announce", Config.getDefaultCatchAnnouncement());
                    boolean skipItemFormat = cs.getBoolean("skip-item-format", false);
                    boolean noDisplay = cs.getBoolean("no-display", false);
                    boolean firework = cs.getBoolean("firework", false);
                    double additionalPrice = cs.getDouble("additional-price", 0D);
                    return new FishRarity(cs.getName(), displayName, isDefault, chance, color, catchHandlers, announcement, skipItemFormat, noDisplay, firework, additionalPrice);
                }).toList();
                ConfigurationSection fishList = fish.getConfigurationSection("fish-list");
                if (fishList != null) {
                    fishList.getKeys(false).stream().map(fishList::getConfigurationSection).filter(Objects::nonNull).forEach(groupByRarity -> {
                        String rarityName = groupByRarity.getName();
                        FishRarity rarity = rarities.stream().filter(fishRarity -> rarityName.equals(fishRarity.getName())).findFirst().orElseThrow(() -> new IllegalStateException("Rarity " + rarityName + " doesn't exist."));
                        List<FishType> fishTypes = groupByRarity.getKeys(false).stream().map(groupByRarity::getConfigurationSection).filter(Objects::nonNull).map(cs -> {
                            List<CatchHandler> catchHandlers = new ArrayList<>(rarity.getCatchHandlers());
                            if (cs.contains("commands")) {
                                catchHandlers.add(new CatchCommandExecutor(cs.getStringList("commands")));
                            }
                            else if (cs.getBoolean("firework", false)) {
                                catchHandlers.add(new CatchFireworkSpawner());
                            }

                            String displayName = cs.getString("display-name");
                            if (displayName == null) {
                                throw new IllegalArgumentException("display-name is missing from " + cs.getCurrentPath() + ".");
                            }

                            String name = cs.getName();
                            double minLength = cs.getDouble("length-min");
                            double maxLength = cs.getDouble("length-max");
                            ItemStack itemStack = loadItemStack(name, cs.getConfigurationSection("icon"));
                            PlayerAnnouncement announcement = PlayerAnnouncement.fromConfigOrDefault(cs, "catch-announce", rarity.getCatchAnnouncement());
                            List<FishCondition> conditions = FishCondition.loadFrom(cs, "conditions");
                            boolean skipItemFormat = cs.getBoolean("skip-item-format", rarity.hasNotFishItemFormat());
                            boolean noDisplay = cs.getBoolean("no-display", rarity.getNoDisplay());
                            boolean firework = cs.getBoolean("firework", rarity.hasCatchFirework());
                            double additionalPrice = rarity.getAdditionalPrice() + cs.getDouble("additional-price", 0D);
                            return new FishType(cs.getName(), rarity, displayName, minLength, maxLength, itemStack, catchHandlers, announcement, conditions, skipItemFormat, noDisplay, firework, additionalPrice);
                        }).toList();
                        map.put(rarity, fishTypes);
                    });
                }
            }
        }
        catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to load " + fishFile);
        }
    }

    private ItemStack loadItemStack(String name, ConfigurationSection cs) {
        if (cs == null) {
            throw new IllegalArgumentException("icon is missing from " + name);
        }

        String id = cs.getString("id");
        if (id == null) {
            throw new IllegalArgumentException("id is missing from " + cs.getCurrentPath() + ".");
        }

        Material material = Material.matchMaterial(id);
        if (material == null) {
            throw new IllegalArgumentException("id " + id + " in " + cs.getCurrentPath() + " is not a valid item ID.");
        }

        int amount = cs.getInt("amount", 1);
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(cs.getStringList("lore").stream().map(Component::text).collect(Collectors.toList()));
        if (cs.contains("enchantments")) {
            cs.getStringList("enchantments").stream().map(string -> string.split("\\|")).forEach(tokens -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(tokens[0]));
                if (enchantment != null) {
                    int level = Integer.parseInt(tokens[1]);
                    itemMeta.addEnchant(enchantment, level, true);
                }
            });
        }

        itemMeta.setUnbreakable(cs.getBoolean("unbreakable", false));
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(cs.getInt("durability", 0));
        }

        if (cs.contains("skull-uuid") && itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("skull-uuid")));
        }

        itemStack.setItemMeta(itemMeta);
        if (cs.contains("skull-texture")) {
            ProtocolLibHooker protocolLib = new ProtocolLibHooker();
            PluginHooker.checkHooked(protocolLib);
            SkullNbtHandler skullNbtHandler = protocolLib.skullNbtHandler;
            if (skullNbtHandler != null) {
                String skullTexture = cs.getString("skull-texture");
                if (skullTexture != null) {
                    itemStack = skullNbtHandler.writeTexture(itemStack, skullTexture);
                }
            }
        }

        return itemStack;
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
    public FishType pickRandomType(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition competition) {
        return pickRandomType(caught, fisher, competition, pickRandomRarity());
    }

    @Nonnull
    public FishType pickRandomType(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition competition, @Nonnull FishRarity rarity) {
        if (!map.containsKey(rarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        List<FishType> types = map.get(rarity).stream().filter(type -> type.getConditions().stream().allMatch(condition -> condition.check(caught, fisher, competition))).toList();
        if (types.isEmpty()) {
            throw new IllegalStateException("No fish type matches given condition");
        }

        return types.get(new Random().nextInt(types.size()));
    }
}
