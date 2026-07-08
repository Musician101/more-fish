package me.elsiff.morefish.serialize.fish.condition;

import me.elsiff.morefish.fish.condition.BiomesCondition;
import me.elsiff.morefish.fish.condition.EnchantmentsCondition;
import me.elsiff.morefish.fish.condition.FishConditions;
import me.elsiff.morefish.fish.condition.LocationYCondition;
import me.elsiff.morefish.fish.condition.PotionEffectsCondition;
import me.elsiff.morefish.fish.condition.RainingCondition;
import me.elsiff.morefish.fish.condition.ThunderingCondition;
import me.elsiff.morefish.fish.condition.TimeCondition;
import me.elsiff.morefish.fish.condition.XpLevelCondition;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.NonRequiredKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class FishConditionsSerializer implements TypeSerializer<FishConditions> {

    private static final NonRequiredKey<BiomesCondition> BIOMES = ConfigKey.nonRequiredKey("biomes", BiomesCondition.class);
    private static final NonRequiredKey<EnchantmentsCondition> ENCHANTMENTS = ConfigKey.nonRequiredKey("enchantments", EnchantmentsCondition.class);
    private static final NonRequiredKey<LocationYCondition> LOCATION_Y = ConfigKey.nonRequiredKey("location-y", LocationYCondition.class);
    private static final NonRequiredKey<PotionEffectsCondition> POTION_EFFECTS = ConfigKey.nonRequiredKey("potion-effects", PotionEffectsCondition.class);
    private static final NonRequiredKey<RainingCondition> RAINING = ConfigKey.nonRequiredKey("raining", RainingCondition.class);
    private static final NonRequiredKey<ThunderingCondition> THUNDERING = ConfigKey.nonRequiredKey("thundering", ThunderingCondition.class);
    private static final NonRequiredKey<TimeCondition> TIME = ConfigKey.nonRequiredKey("time", TimeCondition.class);
    private static final NonRequiredKey<XpLevelCondition> XP_LEVEL = ConfigKey.nonRequiredKey("xp-level", XpLevelCondition.class);

    @Override
    public FishConditions deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishConditions conditions = new FishConditions();
        conditions.biomes(BIOMES.get(node));
        conditions.enchantments(ENCHANTMENTS.get(node));
        conditions.locationY(LOCATION_Y.get(node));
        conditions.potionEffects(POTION_EFFECTS.get(node));
        conditions.raining(RAINING.get(node));
        conditions.thundering(THUNDERING.get(node));
        conditions.time(TIME.get(node));
        conditions.xpLevel(XP_LEVEL.get(node));
        return conditions;
    }

    @Override
    public void serialize(Type type, @Nullable FishConditions obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            BIOMES.set(node, obj.biomes().orElse(null));
            ENCHANTMENTS.set(node, obj.enchantments().orElse(null));
            LOCATION_Y.set(node, obj.locationY().orElse(null));
            POTION_EFFECTS.set(node, obj.potionEffects().orElse(null));
            RAINING.set(node, obj.raining().orElse(null));
            THUNDERING.set(node, obj.thundering().orElse(null));
            TIME.set(node, obj.time().orElse(null));
            XP_LEVEL.set(node, obj.xpLevel().orElse(null));
        }
    }
}
