package me.elsiff.morefish.paper.hooker;

import java.util.Optional;
import me.elsiff.morefish.common.hooker.PluginHooker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public final class VaultHooker extends PaperPluginHooker {

    @Nullable private Economy economy;

    @NotNull
    public Optional<Economy> getEconomy() {
        return Optional.ofNullable(economy);
    }

    @NotNull
    @Override
    public String getPluginName() {
        return "Vault";
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    public void hook() {
        PluginHooker.checkEnabled(this);
        RegisteredServiceProvider<Economy> registration = getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (registration != null) {
            economy = registration.getProvider();
        }

        hasHooked = true;
    }

}
