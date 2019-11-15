package me.elsiff.morefish.configuration.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.condition.BiomeCondition;
import me.elsiff.morefish.fishing.condition.CompetitionCondition;
import me.elsiff.morefish.fishing.condition.EnchantmentCondition;
import me.elsiff.morefish.fishing.condition.FishCondition;
import me.elsiff.morefish.fishing.condition.LocationYCondition;
import me.elsiff.morefish.fishing.condition.McmmoSkillCondition;
import me.elsiff.morefish.fishing.condition.PotionEffectCondition;
import me.elsiff.morefish.fishing.condition.RainingCondition;
import me.elsiff.morefish.fishing.condition.ThunderingCondition;
import me.elsiff.morefish.fishing.condition.TimeCondition;
import me.elsiff.morefish.fishing.condition.TimeCondition.TimeState;
import me.elsiff.morefish.fishing.condition.WorldGuardRegionCondition;
import me.elsiff.morefish.fishing.condition.XpLevelCondition;
import me.elsiff.morefish.hooker.McmmoHooker;
import me.elsiff.morefish.hooker.WorldGuardHooker;
import me.elsiff.morefish.util.NamespacedKeyUtils;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

public final class FishConditionSetLoader implements CustomLoader {

    private static final String DELIMITER = "|";
    private McmmoHooker mcmmoHooker;
    private WorldGuardHooker worldGuardHooker;

    private FishCondition fishConditionFrom(String id, List<String> args) {
        switch (id) {
            case "raining":
                return new RainingCondition(Boolean.parseBoolean(args.get(0)));
            case "thundering":
                return new ThunderingCondition(Boolean.parseBoolean(args.get(0)));
            case "time":
                return new TimeCondition(TimeState.valueOf(args.get(0).toUpperCase()));
            case "biome":
                return new BiomeCondition(args.stream().map(String::toUpperCase).map(Biome::valueOf).collect(Collectors.toSet()));
            case "enchantment":
                return new EnchantmentCondition(Enchantment.getByKey(NamespacedKey.minecraft(args.get(0))), Integer.parseInt(args.get(1)));
            case "level":
                return new XpLevelCondition(Integer.parseInt(args.get(0)));
            case "contest":
                return new CompetitionCondition(FishingCompetition.State.valueOf(args.get(0).toUpperCase()));
            case "potion-effect":
                return new PotionEffectCondition(NamespacedKeyUtils.potionEffectType(args.get(0)), Integer.parseInt(args.get(1)));
            case "location-y":
                return new LocationYCondition(new DoubleRange(Double.parseDouble(args.get(0)), Double.parseDouble(args.get(1))));
            case "mcmmo-skill":
                return new McmmoSkillCondition(mcmmoHooker, args.get(0), Integer.parseInt(args.get(1)));
            case "worldguard-region":
                return new WorldGuardRegionCondition(worldGuardHooker, args.get(0));
            default:
                throw new IllegalStateException("There's no fish condition whose id is " + id);
        }
    }

    public final void init(@Nonnull McmmoHooker mcmmoHooker, @Nonnull WorldGuardHooker worldGuardHooker) {
        this.mcmmoHooker = mcmmoHooker;
        this.worldGuardHooker = worldGuardHooker;
    }

    @Nonnull
    public Set<FishCondition> loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        if (section.contains(path)) {
            return section.getStringList(path).stream().map(string -> {
                List<String> tokens = new ArrayList<>(Arrays.asList(string.split(DELIMITER)));
                String id = tokens.get(0);
                tokens.remove(0);
                return fishConditionFrom(id, tokens);
            }).collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }
}
