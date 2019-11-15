package me.elsiff.morefish.hooker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.MoreFish;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHooker implements PluginHooker {

    @Nullable
    private Economy economy;
    private boolean hasHooked;

    @Nullable
    public final Economy getEconomy() {
        return economy;
    }

    @Nonnull
    public String getPluginName() {
        return "Vault";
    }

    public final boolean hasEconomy() {
        return economy != null;
    }

    public boolean hasHooked() {
        return hasHooked;
    }

    public void hook(@Nonnull MoreFish plugin) {
        PluginHooker.Companion.checkEnabled(this, plugin.getServer().getPluginManager());
        RegisteredServiceProvider<Economy> registration = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (registration != null) {
            economy = registration.getProvider();
        }

        hasHooked = true;
    }

    public void setHasHooked(boolean hasHooked) {
        this.hasHooked = hasHooked;
    }
}
