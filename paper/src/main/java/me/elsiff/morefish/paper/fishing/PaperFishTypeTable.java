package me.elsiff.morefish.paper.fishing;

import com.gmail.nossr50.mcMMO;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import me.elsiff.morefish.common.fishing.FishRarity;
import me.elsiff.morefish.common.fishing.FishTypeTable;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.common.fishing.competition.FishingCompetition;
import me.elsiff.morefish.common.fishing.condition.CompetitionCondition;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.common.hooker.PluginHooker;
import me.elsiff.morefish.common.util.NumberUtils.Range;
import me.elsiff.morefish.paper.fishing.catchhandler.CatchFireworkSpawner;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import me.elsiff.morefish.paper.fishing.condition.LocationYCondition;
import me.elsiff.morefish.paper.hooker.ProtocolLibHooker;
import me.elsiff.morefish.paper.hooker.SkullNbtHandler;
import me.elsiff.morefish.paper.announcement.PaperPlayerAnnouncement;
import me.elsiff.morefish.paper.configuration.Config;
import me.elsiff.morefish.paper.fishing.catchhandler.CatchCommandExecutor;
import me.elsiff.morefish.paper.fishing.condition.BiomeCondition;
import me.elsiff.morefish.paper.fishing.condition.EnchantmentCondition;
import me.elsiff.morefish.paper.fishing.condition.McmmoSkillCondition;
import me.elsiff.morefish.paper.fishing.condition.PotionEffectCondition;
import me.elsiff.morefish.paper.fishing.condition.RainingCondition;
import me.elsiff.morefish.paper.fishing.condition.ThunderingCondition;
import me.elsiff.morefish.paper.fishing.condition.TimeCondition;
import me.elsiff.morefish.paper.fishing.condition.TimeCondition.TimeState;
import me.elsiff.morefish.paper.fishing.condition.XpLevelCondition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public final class PaperFishTypeTable extends FishTypeTable<PaperFishingCompetition, PaperFish, Item, Player, ItemStack, PaperFishType> {

    @Override
    protected List<CatchHandler<PaperFish, Player>> getCatchHandlers(@NotNull JsonObject jsonObject) {
        List<CatchHandler<PaperFish, Player>> catchHandlers = new ArrayList<>();
        if (jsonObject.has("commands")) {
            catchHandlers.add(new CatchCommandExecutor(getStringList(jsonObject.getAsJsonArray("commands"))));
        }
        else if (getOrDefaultFalse(jsonObject, "firework")) {
            catchHandlers.add(new CatchFireworkSpawner());
        }

        return catchHandlers;
    }

    @Override
    protected List<FishCondition<PaperFishingCompetition, Item, Player>> getFishConditions(@NotNull JsonObject jsonObject) {
        String path = "conditions";
        if (jsonObject.has(path)) {
            JsonObject conditions = jsonObject.getAsJsonObject(path);
            return conditions.keySet().stream().<FishCondition<PaperFishingCompetition, Item, Player>>map(key -> {
                String[] args = conditions.get(key).getAsString().split("\\|");
                return switch (key) {
                    case "biome" ->
                            new BiomeCondition(Stream.of(args).map(String::toUpperCase).map(Biome::valueOf).collect(Collectors.toSet()));
                    case "contest" ->
                            new CompetitionCondition<>(FishingCompetition.State.valueOf(args[0].toUpperCase()));
                    case "enchantment" ->
                            new EnchantmentCondition(Optional.ofNullable(Enchantment.getByKey(NamespacedKey.fromString(args[0]))).orElse(Enchantment.ARROW_DAMAGE), Integer.parseInt(args[1]));
                    case "level" -> new XpLevelCondition(Integer.parseInt(args[0]));
                    case "mcmmo-skill" ->
                            new McmmoSkillCondition(getPlugin().getMcmmo(), mcMMO.p.getSkillTools().matchSkill(args[0]), Integer.parseInt(args[1]));
                    case "potion-effect" ->
                            new PotionEffectCondition(Optional.ofNullable(PotionEffectType.getByKey(NamespacedKey.fromString(args[0]))).orElse(PotionEffectType.BLINDNESS), Integer.parseInt(args[1]));
                    case "raining" -> new RainingCondition(Boolean.parseBoolean(args[0]));
                    case "location-y" ->
                            new LocationYCondition(new Range<>(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
                    case "time" -> new TimeCondition(TimeState.valueOf(args[0].toUpperCase()));
                    case "thundering" -> new ThunderingCondition(Boolean.parseBoolean(args[0]));
                    default -> throw new IllegalStateException("There's no fish condition whose id is " + key);
                };
            }).toList();
        }

        return List.of();
    }

    @Override
    protected Path getFishDir() {
        return getPlugin().getDataFolder().toPath().resolve("fish");
    }

    @NotNull
    @Override
    protected PaperFishType getFishType(String name, FishRarity<PaperFishingCompetition, PaperFish, Item, Player> fishRarity, String displayName, double minLength, double maxLength, ItemStack itemStack, List<CatchHandler<PaperFish, Player>> catchHandlers, PlayerAnnouncement<Player> announcement, List<FishCondition<PaperFishingCompetition, Item, Player>> conditions, boolean skipItemFormat, boolean noDisplay, boolean firework, double additionalPrice) {
        return new PaperFishType(name, fishRarity, displayName, minLength, maxLength, itemStack, catchHandlers, announcement, conditions, skipItemFormat, noDisplay, firework, additionalPrice);
    }

    @Override
    protected PlayerAnnouncement<Player> getPlayerAnnouncement(JsonObject jsonObject) {
        return PaperPlayerAnnouncement.fromConfigOrDefault(jsonObject, "catch-announce", Config.getDefaultCatchAnnouncement());
    }

    @Override
    protected @NotNull ItemStack loadItemStack(String name, JsonObject json, String path) {
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

    @Override
    protected void logError(@NotNull String message, @NotNull Throwable throwable) {
        getPlugin().getSLF4JLogger().error(message, throwable);
    }

    @Override
    protected void saveDefaultFile(String fileName) {
        getPlugin().saveResource("fish/" + fileName, false);
    }
}
