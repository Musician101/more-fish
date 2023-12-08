package me.elsiff.morefish.paper.shop;

import me.elsiff.morefish.common.shop.FishShop;
import org.bukkit.configuration.ConfigurationSection;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public class PaperFishShop extends FishShop {

    @Override
    public boolean getEnabled() {
        return getShopConfig().getBoolean("enable");
    }

    @Override
    protected double getPriceMultiplier() {
        return getShopConfig().getDouble("multiplier");
    }

    @Override
    protected boolean getRoundDecimalPoints() {
        return getShopConfig().getBoolean("round-decimal-points");
    }

    private ConfigurationSection getShopConfig() {
        return getPlugin().getConfig().getConfigurationSection("fish-shop");
    }
}
