package me.elsiff.morefish.fishing.condition;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import me.elsiff.morefish.hooker.McmmoHooker;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static me.elsiff.morefish.MoreFish.getPlugin;

public record McmmoSkillsCondition(@NotNull Map<PrimarySkillType, Integer> skills) implements FishCondition {

    public boolean check(@NotNull Item caught, @NotNull Player fisher) {
        McmmoHooker mcmmoHooker = getPlugin().getMcmmo();
        if (mcmmoHooker.hasHooked()) {
            return skills.entrySet().stream().allMatch(e -> mcmmoHooker.skillLevelOf(fisher, e.getKey()) >= e.getValue());
        }

        return true;
    }
}
