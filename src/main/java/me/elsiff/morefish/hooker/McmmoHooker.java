package me.elsiff.morefish.hooker;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import me.elsiff.morefish.MoreFish;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class McmmoHooker implements PluginHooker {

    private boolean hasHooked;

    @NotNull
    public String getPluginName() {
        return "mcMMO";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook(@NotNull MoreFish plugin) {
        this.setHasHooked(true);
    }

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }

    public int skillLevelOf(@NotNull Player player, @NotNull PrimarySkillType skillType) {
        return ExperienceAPI.getLevel(player, skillType);
    }
}
