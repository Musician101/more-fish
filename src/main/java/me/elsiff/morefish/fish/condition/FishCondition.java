package me.elsiff.morefish.fish.condition;

import com.gmail.nossr50.mcMMO;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.elsiff.morefish.fish.condition.TimeCondition.TimeState;
import me.elsiff.morefish.util.NumberUtils.Range;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

public interface FishCondition {

    @NotNull
    static List<FishCondition> loadFrom(@NotNull JsonObject jsonObject, @NotNull String path) {
        if (jsonObject.has(path)) {
            JsonObject conditions = jsonObject.getAsJsonObject(path);
            return conditions.entrySet().stream().map(entry -> {
                JsonElement json = entry.getValue();
                return switch (entry.getKey()) {
                    case "biome" -> {
                        List<Biome> biomes = json.getAsJsonArray().asList().stream().map(s -> loadFromRegistry(Registry.BIOME, s.getAsString())).filter(Objects::nonNull).toList();
                        yield new BiomeCondition(biomes);
                    }
                    case "enchantments" ->
                            new EnchantmentsCondition(loadMappedConditions(json, s -> loadFromRegistry(Registry.ENCHANTMENT, s)));
                    case "level" -> new XpLevelCondition(json.getAsInt());
                    case "location-y" -> {
                        String[] args = json.getAsString().split("-");
                        yield new LocationYCondition(new Range<>(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
                    }
                    case "mcmmo-skills" -> {
                        if (getPlugin().getMcmmo().hasHooked()) {
                            yield new McmmoSkillsCondition(loadMappedConditions(json, mcMMO.p.getSkillTools()::matchSkill));
                        }

                        yield null;
                    }
                    case "potion-effects" ->
                            new PotionEffectCondition(loadMappedConditions(json, s -> loadFromRegistry(Registry.POTION_EFFECT_TYPE, s)));
                    case "raining" -> new RainingCondition(json.getAsBoolean());
                    case "time" -> new TimeCondition(TimeState.get(json.getAsString()).orElse(TimeState.ANY));
                    case "thundering" -> new ThunderingCondition(json.getAsBoolean());
                    default -> {
                        getPlugin().getSLF4JLogger().error("There's no fish condition whose id is {}", entry.getKey());
                        yield null;
                    }
                };
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        return List.of();
    }

    private static <V extends Keyed> V loadFromRegistry(Registry<V> registry, String key) {
        NamespacedKey k = key.contains(":") ? NamespacedKey.fromString(key) : NamespacedKey.minecraft(key);
        return k != null ? registry.get(k) : null;
    }

    private static <K> Map<K, Integer> loadMappedConditions(JsonElement json, Function<String, K> getter) {
        return json.getAsJsonObject().entrySet().stream().map(e -> {
            K skill = getter.apply(e.getKey());
            if (skill == null) {
                return null;
            }

            return new SimpleEntry<>(skill, e.getValue().getAsInt());
        }).filter(Objects::nonNull).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    boolean check(@NotNull Item caught, @NotNull Player fisher);
}
