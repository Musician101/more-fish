package me.elsiff.morefish.fishing.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.condition.TimeCondition.TimeState;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public interface FishCondition {

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    static List<FishCondition> loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        if (section.contains(path)) {
            return section.getStringList(path).stream().map(string -> {
                List<String> tokens = new ArrayList<>(Arrays.asList(string.split("\\|")));
                String id = tokens.get(0);
                tokens.remove(0);
                String[] args = tokens.toArray(new String[0]);
                return switch (id) {
                    case "raining" -> new RainingCondition(Boolean.parseBoolean(args[0]));
                    case "thundering" -> new ThunderingCondition(Boolean.parseBoolean(args[0]));
                    case "time" -> new TimeCondition(TimeState.valueOf(args[0].toUpperCase()));
                    case "biome" -> new BiomeCondition(Stream.of(args).map(String::toUpperCase).map(Biome::valueOf).collect(Collectors.toSet()));
                    case "enchantment" -> new EnchantmentCondition(Enchantment.getByKey(NamespacedKey.minecraft(args[0])), Integer.parseInt(args[1]));
                    case "level" -> new XpLevelCondition(Integer.parseInt(args[0]));
                    case "contest" -> new CompetitionCondition(FishingCompetition.State.valueOf(args[0].toUpperCase()));
                    case "potion-effect" -> new PotionEffectCondition(PotionEffectType.getByName(args[0].split(":")[1]), Integer.parseInt(args[1]));
                    case "location-y" -> new LocationYCondition(new DoubleRange(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
                    default -> throw new IllegalStateException("There's no fish condition whose id is " + id);
                };
            }).collect(Collectors.toList());
        }

        return List.of();
    }

    boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition);
}
