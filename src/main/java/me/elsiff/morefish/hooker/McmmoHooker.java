package me.elsiff.morefish.hooker;

import com.gmail.nossr50.api.ExperienceAPI;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.entity.Player;

public final class McmmoHooker implements PluginHooker {

    private boolean hasHooked;

    @Nonnull
    public String getPluginName() {
        return "mcMMO";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook(@Nonnull MoreFish plugin) {
        this.setHasHooked(true);
    }

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }

    public final int skillLevelOf(@Nonnull Player player, @Nonnull String skillType) {
        return ExperienceAPI.getLevel(player, skillType);
    }
}
