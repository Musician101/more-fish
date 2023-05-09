package me.elsiff.morefish.shop;

import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.hooker.VaultHooker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;

public record FishShop() {

    private Economy getEconomy() {
        VaultHooker vault = getPlugin().getVault();
        if (!vault.hasHooked()) {
            throw new IllegalStateException("Vault must be hooked for fish shop feature");
        }

        if (!vault.hasEconomy()) {
            throw new IllegalStateException("Vault doesn't have economy plugin");
        }

        return vault.getEconomy().orElseThrow(() -> new IllegalStateException("Economy must be enabled"));
    }

    public boolean getEnabled() {
        return getShopConfig().getBoolean("enable");
    }

    private MoreFish getPlugin() {
        return MoreFish.instance();
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
