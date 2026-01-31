package me.elsiff.morefish.serialize.fish.condition;

import com.gmail.nossr50.mcMMO;
import me.elsiff.morefish.fish.condition.McmmoSkillsCondition;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class McmmoSkillConditionSerializer extends FishConditionSerializer<McmmoSkillsCondition> {

    @Override
    public @Nullable McmmoSkillsCondition deserialize(Type type, ConfigurationNode node) {
        if (getPlugin().getMcmmo().hasHooked()) {
            return new McmmoSkillsCondition(loadMappedConditions(node, mcMMO.p.getSkillTools()::matchSkill));
        }

        return null;
    }

    @Override
    public void serialize(Type type, @Nullable McmmoSkillsCondition obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            saveMappedCondition(node, obj.value(), s -> s.name().toLowerCase());
        }
    }
}
