package me.elsiff.morefish.paper.fishing.condition;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import me.elsiff.morefish.common.fishing.condition.FishCondition;
import me.elsiff.morefish.common.hooker.PluginHooker;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import me.elsiff.morefish.paper.hooker.McmmoHooker;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class McmmoSkillCondition implements FishCondition<PaperFishingCompetition, Item, Player> {

    private final McmmoHooker mcmmoHooker;
    private final int minLevel;
    private final PrimarySkillType skillType;

    public McmmoSkillCondition(@NotNull McmmoHooker mcmmoHooker, @NotNull PrimarySkillType skillType, int minLevel) {
        this.mcmmoHooker = mcmmoHooker;
        this.skillType = skillType;
        this.minLevel = minLevel;
    }

    public boolean check(@NotNull Item caught, @NotNull Player fisher, @NotNull PaperFishingCompetition fishingCompetition) {
        PluginHooker.checkEnabled(mcmmoHooker);
        return mcmmoHooker.skillLevelOf(fisher, skillType) >= minLevel;
    }
}
