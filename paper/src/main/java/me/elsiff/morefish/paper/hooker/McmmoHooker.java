package me.elsiff.morefish.paper.hooker;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class McmmoHooker extends PaperPluginHooker {

    @NotNull
    @Override
    public String getPluginName() {
        return "mcMMO";
    }

    @Override
    public void hook() {
        hasHooked = true;
    }

    public int skillLevelOf(@NotNull Player player, @NotNull PrimarySkillType skillType) {
        return ExperienceAPI.getLevel(player, skillType);
    }
}
