package me.elsiff.morefish.hooker;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.Location;

public final class WorldGuardHooker implements PluginHooker {

    @Nonnull
    private final String pluginName = "WorldGuard";
    private boolean hasHooked;

    public final boolean containsLocation(@Nonnull String regionId, @Nonnull Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        ProtectedRegion region = WGBukkit.getRegionManager(location.getWorld()).getRegion(regionId);
        if (region == null) {
            throw new IllegalStateException("Region " + regionId + " doesn't exist");
        }

        return region.contains(x, y, z);
    }

    @Nonnull
    public String getPluginName() {
        return this.pluginName;
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
}
