package me.elsiff.morefish.serialize.fish.condition;

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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class FishConditionsSerializer implements TypeSerializer<FishConditions> {

    @Override
    public FishConditions deserialize(Type type, ConfigurationNode node) throws SerializationException {
        FishConditions conditions = new FishConditions();
        conditions.biomes(node.node("biomes").get(BiomesCondition.class));
        conditions.enchantments(node.node("enchantments").get(EnchantmentsCondition.class));
        conditions.locationY(node.node("location-y").get(LocationYCondition.class));
        conditions.mcmmoSkills(node.node("mcmmo-skills").get(McmmoSkillsCondition.class));
        conditions.potionEffects(node.node("potion-effects").get(PotionEffectsCondition.class));
        conditions.raining(node.node("raining").get(RainingCondition.class));
        conditions.thundering(node.node("thundering").get(ThunderingCondition.class));
        conditions.time(node.node("time").get(TimeCondition.class));
        conditions.xpLevel(node.node("xp-level").get(XpLevelCondition.class));
        return conditions;
    }

    @Override
    public void serialize(Type type, @Nullable FishConditions obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.node("biomes").set(obj.biomes());
            node.node("enchantments").set(obj.enchantments());
            node.node("location-y").set(obj.locationY());
            node.node("mcmmo-skills").set(obj.mcmmoSkills());
            node.node("potion-effects").set(obj.potionEffects());
            node.node("raining").set(obj.raining());
            node.node("thundering").set(obj.thundering());
            node.node("time").set(obj.time());
            node.node("xp-level").set(obj.xpLevel());
        }
    }
}
