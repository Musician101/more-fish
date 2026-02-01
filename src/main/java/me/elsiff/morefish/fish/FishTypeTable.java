package me.elsiff.morefish.fish;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.elsiff.morefish.competition.FishingCompetition;
import me.elsiff.morefish.fish.condition.BiomesCondition;
import me.elsiff.morefish.fish.condition.EnchantmentsCondition;
import me.elsiff.morefish.fish.condition.FishConditions;
import me.elsiff.morefish.fish.condition.LocationYCondition;
import me.elsiff.morefish.fish.condition.McmmoSkillsCondition;
import me.elsiff.morefish.fish.condition.PotionEffectsCondition;
import me.elsiff.morefish.fish.condition.RainingCondition;
import me.elsiff.morefish.fish.condition.ThunderingCondition;
import me.elsiff.morefish.fish.condition.TimeCondition;
import me.elsiff.morefish.fish.condition.XpLevelCondition;
import me.elsiff.morefish.lang.TagResolverUtil;
import me.elsiff.morefish.records.FishRecord;
import me.elsiff.morefish.serialize.fish.FishIconSerializer;
import me.elsiff.morefish.serialize.fish.FishRaritySerializer;
import me.elsiff.morefish.serialize.fish.FishTypeSerializer;
import me.elsiff.morefish.serialize.fish.ItemStackSerializer;
import me.elsiff.morefish.serialize.fish.LuckOfTheSeaModifierSerializer;
import me.elsiff.morefish.serialize.fish.LuckOfTheSeaModifierSerializer.ModifierTypeSerializer;
import me.elsiff.morefish.serialize.fish.PlayerAnnouncementSerializer;
import me.elsiff.morefish.serialize.fish.TextColorSerializer;
import me.elsiff.morefish.serialize.fish.condition.BiomeConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.EnchantmentsConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.FishConditionsSerializer;
import me.elsiff.morefish.serialize.fish.condition.LocationYConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.McmmoSkillConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.PotionEffectsConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.RainingConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.ThunderingConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.TimeConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.XpLevelConditionSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
public final class FishTypeTable {

    private final Multimap<FishRarity, FishType> fishes = HashMultimap.create();
    private final Random random = new Random();
    private final TypeSerializerCollection tsc = TypeSerializerCollection.defaults().childBuilder()
            .register(FishRarity.class, new FishRaritySerializer())
            .register(PlayerAnnouncement.class, new PlayerAnnouncementSerializer())
            .register(FishConditions.class, new FishConditionsSerializer())
            .register(BiomesCondition.class, new BiomeConditionSerializer())
            .register(EnchantmentsCondition.class, new EnchantmentsConditionSerializer())
            .register(LocationYCondition.class, new LocationYConditionSerializer())
            .register(McmmoSkillsCondition.class, new McmmoSkillConditionSerializer())
            .register(PotionEffectsCondition.class, new PotionEffectsConditionSerializer())
            .register(RainingCondition.class, new RainingConditionSerializer())
            .register(ThunderingCondition.class, new ThunderingConditionSerializer())
            .register(TimeCondition.class, new TimeConditionSerializer())
            .register(XpLevelCondition.class, new XpLevelConditionSerializer())
            .register(TextColor.class, new TextColorSerializer())
            .register(LuckOfTheSeaModifier.class, new LuckOfTheSeaModifierSerializer())
            .register(LuckOfTheSeaModifier.Type.class, new ModifierTypeSerializer())
            .register(FishIcon.class, new FishIconSerializer())
            .register(ItemStack.class, new ItemStackSerializer())
            .build();

    private final FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA).withFade(Color.BLUE).withTrail().withFlicker().build();

    public List<FishRarity> getRarities() {
        return new ArrayList<>(fishes.keySet());
    }

    public List<FishType> getTypes() {
        return new ArrayList<>(fishes.values());
    }

    private Path fishDir() {
        return getPlugin().getDataPath().resolve("fish");
    }

    private YamlConfigurationLoader loader(Path path) {
        return loader(path, tsc);
    }

    private YamlConfigurationLoader loader(Path path, TypeSerializerCollection tsc) {
        return YamlConfigurationLoader.builder().defaultOptions(c -> c.serializers(tsc)).path(path).build();
    }

    @Nullable
    private IOException loadRarity(YamlConfigurationLoader loader) {
        try {
            FishRarity rarity = loader.load().require(FishRarity.class);
            IOException exception = loadTypes(rarity);
            if (exception.getSuppressed().length > 0) {
                return exception;
            }

            return null;
        }
        catch (IOException e) {
            return e;
        }
    }

    public void load() throws IOException {
        IOException exception = new IOException("One or more errors occurred while loading fish.");
        if (getPlugin().getConfig().getBoolean("general.load-defaults")) {
            saveDefault("common").ifPresent(exception::addSuppressed);
            saveDefault("epic").ifPresent(exception::addSuppressed);
            saveDefault("junk").ifPresent(exception::addSuppressed);
            saveDefault("legendary").ifPresent(exception::addSuppressed);
            saveDefault("mythic").ifPresent(exception::addSuppressed);
            saveDefault("rare").ifPresent(exception::addSuppressed);
        }

        Path fishDir = fishDir();
        if (Files.notExists(fishDir)) {
            try {
                Files.createDirectories(fishDir);
            }
            catch (IOException e) {
                exception.addSuppressed(e);
            }
        }

        fishes.clear();
        try (Stream<Path> stream = Files.list(fishDir)) {
            stream.filter(path -> !Files.isDirectory(path)).filter(this::isYamlFile).map(this::loader).map(this::loadRarity).filter(Objects::nonNull).forEach(exception::addSuppressed);
        }
        catch (IOException e) {
            exception.addSuppressed(e);
        }

        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
    }

    private boolean isYamlFile(Path path) {
        return path.toString().endsWith(".yml");
    }

    private IOException loadTypes(FishRarity rarity) {
        Path path = fishDir().resolve(rarity.name());
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            }
            catch (IOException e) {
                return e;
            }
        }

        try (Stream<Path> types = Files.list(path)) {
            IOException exception = new IOException("One or more errors occurred while loading fish types for " + rarity.name());
            types.filter(this::isYamlFile).map(p -> {
                try {
                    YamlConfigurationLoader loader = loader(p, tsc.childBuilder().register(FishType.class, new FishTypeSerializer(rarity)).build());
                    fishes.put(rarity, loader.load().require(FishType.class));
                    return null;
                }
                catch (ConfigurateException e) {
                    return e;
                }
            }).filter(Objects::nonNull).forEach(exception::addSuppressed);
            return exception;
        }
        catch (IOException e) {
            return e;
        }
    }

    private Optional<IOException> saveDefault(String name) {
        getPlugin().saveResource("fish/" + name + ".yml", false);
        try (JarFile jar = new JarFile(getPlugin().getClass().getProtectionDomain().getCodeSource().getLocation().getPath())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith("fish/" + name + "/") && entryName.endsWith(".yml")) {
                    getPlugin().saveResource(entryName, false);
                }
            }
        }
        catch (IOException e) {
            return Optional.of(e);
        }

        return Optional.empty();
    }

    public FishRarity pickRandomRarity(int luckOfTheSeasLevel) {
        int weightSum = getRarities().stream().mapToInt(r -> r.modifiedWeight(luckOfTheSeasLevel)).sum();
        List<FishRarity> rarities = getRarities();
        rarities.sort(FishRarity::compareTo);
        int randomVal = random.nextInt(weightSum);
        for (FishRarity rarity : getRarities()) {
            randomVal -= rarity.modifiedWeight(luckOfTheSeasLevel);
            if (randomVal < 0) {
                return rarity;
            }
        }

        throw new IllegalStateException("This is bad. We some how generated a number greater than the sum of all weights.");
    }

    public void simulateCatch(Player player, FishType fishType) {
        Fish fish = fishType.generateFish();
        Item item = player.getWorld().spawn(player.getLocation(), Item.class, i -> i.setItemStack(fish.type().icon().createItemStack(fish, player)));
        if (checkConditions(fishType, item, player)) {
            processCatchHandlers(player, fish, false);
            return;
        }

        item.remove();
    }

    private boolean checkConditions(FishType fishType, Item item, Player player) {
        return fishType.conditions().check(item, player) && fishType.rarity().conditions().check(item, player);
    }

    public void caughtFish(Item caught, Player player, boolean isCompetition) {
        int luckOfTheSeaLevel = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
        FishType type = pickRandomType(caught, player, luckOfTheSeaLevel);
        Fish fish = type.generateFish();
        processCatchHandlers(player, fish, isCompetition);
        Server server = player.getServer();
        Stream.concat(fish.type().commands().stream(), fish.rarity().commands().stream()).forEach(c -> server.dispatchCommand(server.getConsoleSender(), c.replace("@p", player.getName())));
        if (fish.type().firework()) {
            player.getWorld().spawn(player.getLocation(), Firework.class, firework -> {
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                meta.setPower(1);
                firework.setFireworkMeta(meta);
            });
        }

        ItemStack fishItem = type.icon().createItemStack(fish, player);
        caught.setItemStack(fishItem);
        caught.setCanMobPickup(false);
        caught.setCanPlayerPickup(false);
        if (isCompetition && !getPlugin().getFishBags().addFish(player, fishItem)) {
            World world = player.getWorld();
            world.dropItem(player.getLocation(), fishItem);
        }

        caught.remove();
    }

    @SuppressWarnings("NullableProblems")
    private void processCatchHandlers(Player player, Fish fish, boolean competition) {
        List<World> contestDisabledWorlds = getPlugin().getConfig().getStringList("general.contest-disabled-worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).toList();
        if (!competition || contestDisabledWorlds.contains(player.getWorld())) {
            return;
        }

        NodePath announcementPath = NodePath.path("main", "announcement");
        FishingCompetition fc = getPlugin().getCompetition();
        if (fc.isEnabled()) {
            if (fc.willBeNewFirst(player, fish)) {
                broadcastCatch(announcementPath.withAppendedChild("new-1st"), player, fish);
            }

            FishRecord record = new FishRecord(player.getUniqueId(), fish, System.currentTimeMillis());
            fc.add(record);
            getPlugin().getFishingLogs().add(record);
        }

        if (!fish.type().announcement().receiversOf(player).isEmpty()) {
            broadcastCatch(announcementPath.withAppendedChild("catch"), player, fish);
        }
    }

    private void broadcastCatch(NodePath path, Player player, Fish fish) {
        Component msg = lang().getComponent(path, TagResolverUtil.catcher(player, fish));
        fish.type().announcement().receiversOf(player).forEach(p -> p.sendMessage(msg));
    }

    public FishType pickRandomType(Item caught, Player fisher) {
        return pickRandomType(caught, fisher, 0);
    }

    public FishType pickRandomType(Item caught, Player fisher, int luckOfTheSeaLevel) {
        FishRarity rarity = pickRandomRarity(luckOfTheSeaLevel);
        if (!fishes.containsKey(rarity)) {
            throw new IllegalStateException("Rarity must be contained in the table");
        }

        List<FishType> types = fishes.get(rarity).stream().filter(type -> checkConditions(type, caught, fisher)).toList();
        return types.get(random.nextInt(types.size()));
    }

    public List<FishType> getTypes(FishRarity rarity) {
        return new ArrayList<>(fishes.get(rarity));
    }

    public void saveRarity(FishRarity rarity) throws IOException {
        YamlConfigurationLoader loader = loader(fishDir().resolve(rarity.name() + ".yml"));
        ConfigurationNode node = CommentedConfigurationNode.root(loader.defaultOptions());
        node.set(rarity);
        loader.save(node);
    }

    public void saveType(FishType type) throws IOException {
        saveType(type, null);
    }

    public void saveType(FishType type, @Nullable FishRarity oldRarity) throws IOException {
        Path rarityDir = fishDir().resolve(type.rarity().name());
        YamlConfigurationLoader loader = loader(rarityDir.resolve(type.rarity().name() + "/" + type.name() + ".yml"));
        ConfigurationNode node = CommentedConfigurationNode.root(loader.defaultOptions());
        node.set(type);
        loader.save(node);
        if (oldRarity != null && !oldRarity.equals(type.rarity())) {
            Files.delete(fishDir().resolve(oldRarity.name() + "/" + type.name() + ".yml"));
        }
    }

    public void deleteRarity(FishRarity rarity) throws IOException {
        if (fishes.get(rarity).isEmpty()) {
            Files.delete(fishDir().resolve(rarity.name()));
            Files.delete(fishDir().resolve(rarity.name() + ".yml"));
        }
    }

    public void deleteType(FishType fishType) throws IOException {
        fishes.remove(fishType.rarity(), fishType);
        Files.delete(fishDir().resolve(Path.of(fishType.rarity().name(), fishType.name() + ".yml")));
    }
}
