package io.musician101.morefish.spigot.hooker;

import io.musician101.morefish.common.hooker.PluginHooker;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class SpigotVaultHooker implements SpigotPluginHooker {

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

    public void hook() {
        PluginHooker.checkEnabled(this);
        RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registration != null) {
            economy = registration.getProvider();
        }

        hasHooked = true;
    }

    public void setHasHooked(boolean hasHooked) {
        this.hasHooked = hasHooked;
    }
}
