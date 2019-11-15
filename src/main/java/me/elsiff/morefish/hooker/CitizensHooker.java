package me.elsiff.morefish.hooker;

import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.shop.FishShopKeeperTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

public final class CitizensHooker implements PluginHooker {

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

    public void hook(@Nonnull MoreFish plugin) {
        this.traitInfo = TraitInfo.create(FishShopKeeperTrait.class);
        CitizensAPI.getTraitFactory().registerTrait(traitInfo);
        FishShopKeeperTrait.init(plugin.getFishShop());
        setHasHooked(true);
    }

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }
}
