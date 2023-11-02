package me.elsiff.morefish.fishing.condition;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.hooker.McmmoHooker;
import me.elsiff.morefish.hooker.PluginHooker;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class McmmoSkillCondition implements FishCondition {

    private final McmmoHooker mcmmoHooker;
    private final int minLevel;
    private final PrimarySkillType skillType;

    public McmmoSkillCondition(@NotNull McmmoHooker mcmmoHooker, @NotNull PrimarySkillType skillType, int minLevel) {
        this.mcmmoHooker = mcmmoHooker;
        this.skillType = skillType;
        this.minLevel = minLevel;
    }

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull FishingCompetition fishingCompetition) {
        PluginHooker.checkEnabled(mcmmoHooker, fisher.getServer().getPluginManager());
        return mcmmoHooker.skillLevelOf(fisher, skillType) >= minLevel;
    }
}
