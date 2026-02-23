package me.elsiff.morefish.fish;

import me.elsiff.morefish.competition.FishingCompetition;
import me.elsiff.morefish.lang.ArgumentUtil;
import me.elsiff.morefish.lang.TagResolverUtil;
import me.elsiff.morefish.records.FishRecord;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishType extends FishAbstract<FishType> {

    private final FishIcon icon;
    private FishRarity rarity;
    private double minLength = 0.1;
    private double maxLength = 1;

    @SuppressWarnings("UnstableApiUsage")
    public FishType(NamespacedKey key, String displayName, FishRarity rarity) {
        this(key, rarity, displayName, new FishIcon(ItemType.SALMON.createItemStack()));
    }

    public FishType(NamespacedKey key, FishRarity rarity, String displayName, FishIcon icon) {
        super("fish-type", key, displayName);
        this.rarity = rarity;
        this.icon = icon;
    }

    public FishType(NamespacedKey key, FishRarity rarity, String displayName, float priceMultiplier, FishIcon icon) {
        super("fish-type", key, displayName, priceMultiplier);
        this.rarity = rarity;
        this.icon = icon;
    }

    public void maxLength(double maxLength) {
        this.maxLength = maxLength;
    }

    public void minLength(double minLength) {
        this.minLength = minLength;
    }

    public void rarity(FishRarity rarity) {
        this.rarity = rarity;
    }

    private double clamp(double value, double min, double max) {
        double var7 = Math.min(value, max);
        return Math.max(var7, min);
    }

    private double floorToTwoDecimalPlaces(double value) {
        double var3 = value * 10;
        return Math.floor(var3) / 10;
    }

    public Fish generateFish(double length) {
        if (minLength > length && length > maxLength) {
            throw new IllegalArgumentException("Length is outside the min/max range for " + getKey());
        }

        return new Fish(this, length);
    }

    public Fish generateFish() {
        if (minLength > maxLength) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = minLength + new Random().nextDouble() * (maxLength - minLength);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), minLength, maxLength);
        return new Fish(this, length);
    }

    @Override
    public int compareTo(FishType o) {
        return getKey().compareTo(o.getKey());
    }

    public FishRarity rarity() {
        return rarity;
    }

    public double minLength() {
        return minLength;
    }

    public double maxLength() {
        return maxLength;
    }

    public FishIcon icon() {
        return icon;
    }

    public void caught(Item caught, Player player) {
        caught.setCanMobPickup(false);
        caught.setCanPlayerPickup(false);
        caught.setInvulnerable(true);
        Fish fish = generateFish();
        processCatchHandlers(player, fish);
        Stream.concat(fish.type().commands().stream(), fish.rarity().commands().stream()).forEach(c -> processCommand(player, c));
        if (fish.type().firework()) {
            player.getWorld().spawn(player.getLocation(), Firework.class, firework -> {
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA).withFade(Color.BLUE).withTrail().withFlicker().build());
                meta.setPower(1);
                firework.setFireworkMeta(meta);
            });
        }

        ItemStack fishItem = icon().createItemStack(fish, player);
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

        String announcementPath = "morefish.main.announcement.";
        FishingCompetition fc = getPlugin().getCompetition();
        if (fc.isEnabled()) {
            if (fc.willBeNewFirst(player, fish)) {
                broadcastCatch(announcementPath + "new-1st", player, fish);
            }

            FishRecord record = new FishRecord(player.getUniqueId(), fish, System.currentTimeMillis());
            fc.add(record);
            getPlugin().getFishingLogs().add(record);
        }

        if (fish.type().announcement().type() != PlayerAnnouncement.Type.NONE) {
            broadcastCatch(announcementPath + "catch", player, fish);
        }
    }

    private void broadcastCatch(String key, Player player, Fish fish) {
        Component msg = Component.translatable(key, ArgumentUtil.player(player), ArgumentUtil.fish(fish));
        Audience.audience(fish.type().announcement().receiversOf(player)).sendMessage(msg);
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            String value = arguments.popOr("fish-type needs at least 1 argument.").value();
            return switch (value) {
                case "fish-rarity" -> Tag.selfClosingInserting(Component.text(rarity.getKey().asString()));
                case "max-length" -> TagResolverUtil.numberTag("max-length", maxLength, arguments, ctx);
                case "min-length" -> TagResolverUtil.numberTag("min-length", minLength, arguments, ctx);
                case "name-with-rarity" ->
                        Tag.preProcessParsed((noDisplay() ? "" : rarity().displayName().toUpperCase() + " ") + displayName());
                default -> super.resolve(name, arguments, ctx);
            };
        }

        return null;
    }
}
