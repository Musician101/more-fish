package me.elsiff.morefish.fishing.condition;

import com.gmail.nossr50.mcMMO;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.condition.TimeCondition.TimeState;
import me.elsiff.morefish.util.NumberUtils.Range;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import static me.elsiff.morefish.MoreFish.getPlugin;

public interface FishCondition {

    @Nonnull
    static List<FishCondition> loadFrom(@Nonnull JsonObject jsonObject, @Nonnull String path) {
        if (jsonObject.has(path)) {
            JsonObject conditions = jsonObject.getAsJsonObject(path);
            return conditions.keySet().stream().map(key -> {
                String[] args = conditions.get(key).getAsString().split("\\|");
                return switch (key) {
                    case "biome" ->
                            new BiomeCondition(Stream.of(args).map(String::toUpperCase).map(Biome::valueOf).collect(Collectors.toSet()));
                    case "contest" -> new CompetitionCondition(FishingCompetition.State.valueOf(args[0].toUpperCase()));
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

    boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition);
}
