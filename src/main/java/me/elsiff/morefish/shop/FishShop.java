package me.elsiff.morefish.shop;

import me.elsiff.morefish.fishing.Fish;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public record FishShop() {

    public boolean getEnabled() {
        return getShopConfig().getBoolean("enable");
    }

    private double getPriceMultiplier() {
        return getShopConfig().getDouble("multiplier");
    }

    private ConfigurationSection getShopConfig() {
        return getPlugin().getConfig().getConfigurationSection("fish-shop");
    }

    public double priceOf(@NotNull Fish fish) {
        double rarityPrice = fish.type().additionalPrice();
        return this.getPriceMultiplier() * fish.length() + rarityPrice;
    }
}
