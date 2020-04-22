package me.elsiff.morefish.fishing.condition;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.hooker.McmmoHooker;
import me.elsiff.morefish.hooker.PluginHooker;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class McmmoSkillCondition implements FishCondition {

    private final McmmoHooker mcmmoHooker;
    private final int minLevel;
    private final PrimarySkillType skillType;

    public McmmoSkillCondition(@Nonnull McmmoHooker mcmmoHooker, @Nonnull PrimarySkillType skillType, int minLevel) {
        this.mcmmoHooker = mcmmoHooker;
        this.skillType = skillType;
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher, @Nonnull FishingCompetition fishingCompetition) {
        PluginHooker.Companion.checkEnabled(mcmmoHooker, fisher.getServer().getPluginManager());
        return mcmmoHooker.skillLevelOf(fisher, skillType) >= minLevel;
    }
}
