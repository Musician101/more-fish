package me.elsiff.morefish.hooker;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import me.elsiff.morefish.util.EnumUtils;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public final class McmmoHooker implements PluginHooker {

    private boolean hasHooked;

    public String getPluginName() {
        return "mcMMO";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook() {
        if (canHook()) {
            hasHooked = true;
        }
    }

    public int skillLevelOf(Player player, PrimarySkillType skillType) {
        return ExperienceAPI.getLevel(player, skillType);
    }

    @Nullable
    public PrimarySkillType getSkill(String skillName) {
        Optional<PrimarySkillType> skillType = EnumUtils.get(skillName, PrimarySkillType.class);
        return skillType.orElse(null);

    }
}
