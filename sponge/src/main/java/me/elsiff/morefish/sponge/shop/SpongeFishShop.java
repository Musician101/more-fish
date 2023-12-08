package me.elsiff.morefish.sponge.shop;

import me.elsiff.morefish.common.shop.FishShop;
import org.spongepowered.configurate.ConfigurationNode;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public class SpongeFishShop extends FishShop {

    @Override
    public boolean getEnabled() {
        return getShopConfig().node("enable").getBoolean();
    }

    @Override
    protected double getPriceMultiplier() {
        return getShopConfig().node("multiplier").getDouble();
    }

    @Override
    protected boolean getRoundDecimalPoints() {
        return getShopConfig().node("round-decimal-points").getBoolean();
    }

    private ConfigurationNode getShopConfig() {
        return getPlugin().getConfig().node("fish-shop");
    }
}
