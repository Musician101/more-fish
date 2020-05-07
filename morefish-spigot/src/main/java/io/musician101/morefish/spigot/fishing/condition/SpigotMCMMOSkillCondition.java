package io.musician101.morefish.spigot.fishing.condition;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import io.musician101.morefish.common.hooker.PluginHooker;
import io.musician101.morefish.spigot.hooker.SpigotMCMMOHooker;
import javax.annotation.Nonnull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class SpigotMCMMOSkillCondition implements SpigotFishCondition {

    private final SpigotMCMMOHooker mcmmoHooker;
    private final int minLevel;
    private final PrimarySkillType skillType;

    public SpigotMCMMOSkillCondition(@Nonnull SpigotMCMMOHooker mcmmoHooker, @Nonnull PrimarySkillType skillType, int minLevel) {
        this.mcmmoHooker = mcmmoHooker;
        this.skillType = skillType;
        this.minLevel = minLevel;
    }

    public boolean check(@Nonnull Item caught, @Nonnull Player fisher) {
        PluginHooker.checkEnabled(mcmmoHooker);
        return mcmmoHooker.skillLevelOf(fisher, skillType) >= minLevel;
    }
}
