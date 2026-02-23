package me.elsiff.morefish.hooker;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public final class VaultHooker implements PluginHooker {

    @Nullable
    private Economy economy;
    private boolean hasHooked;

    public Optional<Economy> getEconomy() {
        return Optional.ofNullable(economy);
    }

    public String getPluginName() {
        return "Vault";
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    public boolean hasHooked() {
        return hasHooked;
    }

    public void hook() {
        if (canHook()) {
            RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (registration != null) {
                economy = registration.getProvider();
            }

            hasHooked = true;
        }
    }

}
