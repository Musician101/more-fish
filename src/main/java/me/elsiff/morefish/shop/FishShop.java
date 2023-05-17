package me.elsiff.morefish.shop;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.configuration.ConfigurationSection;

import static me.elsiff.morefish.MoreFish.getPlugin;

public record FishShop() {

    public boolean getEnabled() {
        return getShopConfig().getBoolean("enable");
    }

    private double getPriceMultiplier() {
        return getShopConfig().getDouble("multiplier");
    }

    private boolean getRoundDecimalPoints() {
        return getShopConfig().getBoolean("round-decimal-points");
    }

    private ConfigurationSection getShopConfig() {
        return getPlugin().getConfig().getConfigurationSection("fish-shop");
    }

    public double priceOf(@Nonnull Fish fish) {
        double rarityPrice = fish.type().additionalPrice();
        double price = this.getPriceMultiplier() * fish.length() + rarityPrice;
        return getRoundDecimalPoints() ? Math.floor(price) : price;
    }
}
