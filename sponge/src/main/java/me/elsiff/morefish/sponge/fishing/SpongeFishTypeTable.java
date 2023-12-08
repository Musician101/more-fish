package me.elsiff.morefish.sponge.fishing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
import me.elsiff.morefish.common.fishing.condition.TimeState;
import me.elsiff.morefish.common.util.NumberUtils.Range;
import me.elsiff.morefish.sponge.announcement.SpongePlayerAnnouncement;
import me.elsiff.morefish.sponge.configuration.Config;
import me.elsiff.morefish.sponge.fishing.catchhandler.CatchCommandExecutor;
import me.elsiff.morefish.sponge.fishing.catchhandler.CatchFireworkSpawner;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import me.elsiff.morefish.sponge.fishing.condition.BiomeCondition;
import me.elsiff.morefish.sponge.fishing.condition.EnchantmentCondition;
import me.elsiff.morefish.sponge.fishing.condition.LocationYCondition;
import me.elsiff.morefish.sponge.fishing.condition.PotionEffectCondition;
import me.elsiff.morefish.sponge.fishing.condition.RainingCondition;
import me.elsiff.morefish.sponge.fishing.condition.ThunderingCondition;
import me.elsiff.morefish.sponge.fishing.condition.TimeCondition;
import me.elsiff.morefish.sponge.fishing.condition.XpLevelCondition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.world.biome.Biomes;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public final class SpongeFishTypeTable extends FishTypeTable<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer, ItemStack, SpongeFishType> {

    @Override
    protected List<CatchHandler<SpongeFish, ServerPlayer>> getCatchHandlers(@NotNull JsonObject jsonObject) {
        List<CatchHandler<SpongeFish, ServerPlayer>> catchHandlers = new ArrayList<>();
        if (jsonObject.has("commands")) {
            catchHandlers.add(new CatchCommandExecutor(getStringList(jsonObject.getAsJsonArray("commands"))));
        }
        else if (getOrDefaultFalse(jsonObject, "firework")) {
            catchHandlers.add(new CatchFireworkSpawner());
        }

        return catchHandlers;
    }

    @Override
    protected List<FishCondition<SpongeFishingCompetition, Item, ServerPlayer>> getFishConditions(@NotNull JsonObject jsonObject) {
        String path = "conditions";
        if (jsonObject.has(path)) {
            JsonObject conditions = jsonObject.getAsJsonObject(path);
            return conditions.keySet().stream().<FishCondition<SpongeFishingCompetition, Item, ServerPlayer>>map(key -> {
                String[] args = conditions.get(key).getAsString().split("\\|");
                return switch (key) {
                    case "biome" ->
                            new BiomeCondition(Stream.of(args).map(String::toUpperCase).flatMap(s -> Sponge.server().worldManager().worlds().stream().map(Biomes::registry).map(w -> w.findValue(ResourceKey.resolve(s))).filter(Optional::isPresent).map(Optional::get)).collect(Collectors.toSet()));
                    case "contest" ->
                            new CompetitionCondition<>(FishingCompetition.State.valueOf(args[0].toUpperCase()));
                    case "enchantment" ->
                            new EnchantmentCondition(EnchantmentTypes.registry().value(ResourceKey.resolve(args[0])), Integer.parseInt(args[1]));
                    case "level" -> new XpLevelCondition(Integer.parseInt(args[0]));
                    case "potion-effect" ->
                            new PotionEffectCondition(PotionEffectTypes.registry().value(ResourceKey.resolve(args[0])), Integer.parseInt(args[1]));
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
        return getPlugin().getConfigDir().resolve("fish");
    }

    @NotNull
    @Override
    protected SpongeFishType getFishType(String name, FishRarity<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer> fishRarity, String displayName, double minLength, double maxLength, ItemStack itemStack, List<CatchHandler<SpongeFish, ServerPlayer>> catchHandlers, PlayerAnnouncement<ServerPlayer> announcement, List<FishCondition<SpongeFishingCompetition, Item, ServerPlayer>> conditions, boolean skipItemFormat, boolean noDisplay, boolean firework, double additionalPrice) {
        return new SpongeFishType(name, fishRarity, displayName, minLength, maxLength, itemStack, catchHandlers, announcement, conditions, skipItemFormat, noDisplay, firework, additionalPrice);
    }

    @Override
    protected PlayerAnnouncement<ServerPlayer> getPlayerAnnouncement(JsonObject jsonObject) {
        return SpongePlayerAnnouncement.fromConfigOrDefault(jsonObject, "catch-announce", Config.getDefaultCatchAnnouncement());
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

        ItemType material = ItemTypes.registry().value(ResourceKey.resolve(id));
        if (material == null) {
            throw new IllegalArgumentException("id " + id + " in " + path + ".icon is not a valid item ID.");
        }

        int amount = json.has("amount") ? json.get("amount").getAsInt() : 1;
        ItemStack.Builder builder = ItemStack.builder().itemType(material).quantity(amount);
        if (json.has("lore")) {
            List<Component> lore = StreamSupport.stream(json.getAsJsonArray("lore").spliterator(), false).map(content -> GsonComponentSerializer.gson().deserialize(content.toString())).toList();
            builder.add(Keys.LORE, lore);
        }

        if (json.has("enchantments")) {
            List<Enchantment> enchantments = json.getAsJsonArray("enchantments").asList().stream().map(JsonElement::getAsString).map(string -> string.split("\\|")).map(tokens -> EnchantmentTypes.registry().findValue(ResourceKey.resolve(tokens[0])).map(enchantment -> {
                int level = Integer.parseInt(tokens[1]);
                return Enchantment.of(enchantment, level);
            })).filter(Optional::isPresent).map(Optional::get).toList();
            builder.add(Keys.APPLIED_ENCHANTMENTS, enchantments);
        }

        builder.add(Keys.IS_UNBREAKABLE, json.has("unbreakable") && json.get("unbreakable").getAsBoolean());
        builder.add(Keys.ITEM_DURABILITY, json.has("durability") ? json.get("durability").getAsInt() : 0);
        if (json.has("skull-uuid")) {
            UUID skullUUID = UUID.fromString(json.get("skull-uuid").getAsString());
            try {
                builder.add(Keys.GAME_PROFILE, Sponge.server().gameProfileManager().profile(skullUUID).get());
            }
            catch (InterruptedException | ExecutionException e) {
                logError("Failed to load player head for " + path + ".icon.skull-uuid: " + skullUUID, e);
            }
        }

        if (json.has("skull-texture")) {
            builder.add(Keys.SKIN_PROFILE_PROPERTY, ProfileProperty.of(ProfileProperty.TEXTURES, json.get("skull-texture").getAsString()));
        }

        return builder.build();
    }

    @Override
    protected void logError(@NotNull String message, @NotNull Throwable throwable) {
        getPlugin().getLogger().error(message, throwable);
    }

    @Override
    protected void saveDefaultFile(String fileName) {
        URI uri = URI.create("fish/" + fileName);
        Path path = Path.of(uri);
        if (Files.notExists(path)) {
            getPlugin().getPluginContainer().openResource(uri);
        }
    }
}
