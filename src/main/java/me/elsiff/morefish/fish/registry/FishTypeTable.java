package me.elsiff.morefish.fish.registry;

import me.elsiff.morefish.competition.FishingCompetition;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.lang.TagResolverUtil;
import me.elsiff.morefish.records.FishRecord;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

//TODO reducing this class to a Utility class since loading/saving has been separated into different classes
@Deprecated
@NullMarked
public final class FishTypeTable {

    private final FishRarities rarities = new FishRarities();
    private final FishTypes types = new FishTypes();
    private final Random random = new Random();
    private final FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA).withFade(Color.BLUE).withTrail().withFlicker().build();

    public List<FishRarity> getRarities() {
        return new ArrayList<>(rarities.values());
    }

    public List<FishType> getTypes() {
        return new ArrayList<>(types.values());
    }

    public FishRarities rarities() {
        return rarities;
    }

    public FishTypes types() {
        return types;
    }

    public void load() throws IOException {
        rarities.load();
        types.load();
    }

    public void caughtFish(Item caught, Player player) {
        int luckOfTheSeaLevel = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
        caughtFish(caught, player, types.pickRandomType(caught, player, luckOfTheSeaLevel, random));
    }

    public void caughtFish(Item caught, Player player, FishType type) {
        caught.setCanMobPickup(false);
        caught.setCanPlayerPickup(false);
        caught.setInvulnerable(true);
        Fish fish = type.generateFish();
        processCatchHandlers(player, fish);
        Stream.concat(fish.type().commands().stream(), fish.rarity().commands().stream()).forEach(c -> processCommand(player, c));
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
        if (!getPlugin().getFishBags().addFish(player, fishItem)) {
            World world = player.getWorld();
            world.dropItem(player.getLocation(), fishItem);
        }

        caught.remove();
    }

    private void processCommand(Player player, String command) {
        Server server = player.getServer();
        command = command.replace("@p", player.getName());
        Location location = player.getLocation();
        command = command.replace("!playerLocation", location.x() + " " + location.y() + " " + location.z());
        server.dispatchCommand(server.getConsoleSender(), command);
    }

    @SuppressWarnings("NullableProblems")
    private void processCatchHandlers(Player player, Fish fish) {
        List<World> contestDisabledWorlds = getPlugin().getConfig().getStringList("general.contest-disabled-worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).toList();
        if (contestDisabledWorlds.contains(player.getWorld())) {
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
        Audience.audience(fish.type().announcement().receiversOf(player)).sendMessage(msg);
    }

    public List<FishType> getTypes(FishRarity rarity) {
        return types.get(rarity);
    }

    public void saveRarity(FishRarity rarity) throws IOException {
        rarities.save(rarity);
    }

    public void saveType(FishType type) throws IOException {
        types.save(type);
    }

    public void deleteRarity(FishRarity rarity) throws IOException {
        rarities.delete(rarity);
    }

    public void deleteType(FishType type) throws IOException {
        types.delete(type);
    }

    public Optional<FishRarity> getRarity(@Nullable NamespacedKey key) {
        if (key == null) {
            return Optional.empty();
        }

        return rarities.get(key);
    }
}
