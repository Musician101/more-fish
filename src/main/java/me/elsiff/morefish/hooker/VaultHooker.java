package me.elsiff.morefish.hooker;

import java.util.Optional;
import me.elsiff.morefish.MoreFish;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class VaultHooker implements PluginHooker {

    @Nullable
    private Economy economy;
    private boolean hasHooked;

    @NotNull
    public Optional<Economy> getEconomy() {
        return Optional.ofNullable(economy);
    }

    @NotNull
    public String getPluginName() {
        return "Vault";
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    public boolean hasHooked() {
        return hasHooked;
    }

    public void hook(@NotNull MoreFish plugin) {
        PluginHooker.checkEnabled(this, plugin.getServer().getPluginManager());
        RegisteredServiceProvider<Economy> registration = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (registration != null) {
            economy = registration.getProvider();
        }

        hasHooked = true;
    }

}
