package me.elsiff.morefish.sponge.hooker;

import java.util.Optional;
import me.elsiff.morefish.common.hooker.PluginHooker;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;

public final class EconomyHooker extends SpongePluginHooker {

    @NotNull
    public Optional<EconomyService> getEconomy() {
        return Sponge.server().serviceProvider().economyService();
    }

    @NotNull
    @Override
    public String getPluginName() {
        return "spongeapi";
    }

    public boolean hasEconomy() {
        return getEconomy().isPresent();
    }

    public void hook() {
        PluginHooker.checkEnabled(this);
        hasHooked = true;
    }
}
