package io.musician101.morefish.sponge.hooker;

import io.musician101.morefish.common.hooker.PluginHooker;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;

public final class SpongeEconomyHooker implements SpongePluginHooker {

    @Nullable
    private EconomyService economy;
    private boolean hasHooked;

    @Nullable
    public final EconomyService getEconomy() {
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
        Sponge.serviceProvider().provide(EconomyService.class).ifPresent(economyService -> this.economy = economyService);
        hasHooked = true;
    }

    public void setHasHooked(boolean hasHooked) {
        this.hasHooked = hasHooked;
    }
}
