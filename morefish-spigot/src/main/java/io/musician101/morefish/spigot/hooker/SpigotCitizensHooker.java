package io.musician101.morefish.spigot.hooker;

import io.musician101.morefish.spigot.shop.FishShopKeeperTrait;
import javax.annotation.Nonnull;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

public final class SpigotCitizensHooker implements SpigotPluginHooker {

    private boolean hasHooked;
    private TraitInfo traitInfo;

    public final void dispose() {
        CitizensAPI.getTraitFactory().deregisterTrait(traitInfo);
    }

    @Nonnull
    public String getPluginName() {
        return "Citizens";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook() {
        this.traitInfo = TraitInfo.create(FishShopKeeperTrait.class);
        CitizensAPI.getTraitFactory().registerTrait(traitInfo);
        setHasHooked(true);
    }

    public void setHasHooked(boolean hasHooked) {
        this.hasHooked = hasHooked;
    }
}
